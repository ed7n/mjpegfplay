// @formatter:off
package eden.mjpegfplay.presenter.worker;

import eden.mjpegfplay.presenter.ApplicationInstance;
import eden.mjpegfplay.presenter.NullPresenter;
import eden.mjpegfplay.presenter.Presenter;
import eden.mjpegfplay.presenter.exception.BadMetadataException;
import eden.mjpegfplay.presenter.exception.BadParameterException;
import eden.mjpegfplay.presenter.exception.MalformedSequenceException;

import eden.common.audio.OutputMixer;
import eden.common.audio.OutputSource;
import eden.common.clock.SimpleSyncroTimer;
import eden.common.clock.SyncroClock;
import eden.common.io.ConfigFileReader;
import eden.common.io.active.FileFrameLens;
import eden.common.model.sequence.FileFrameSequence;
import eden.common.model.sequence.Sequence;
import eden.common.video.render.MultiLensFrameRenderer;
import eden.common.video.render.RendererComponent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import static eden.mjpegfplay.model.TransportConstants.*;
import static eden.mjpegfplay.presenter.ApplicationInstance.METADATA_FILE;
import static eden.mjpegfplay.view.FrontPanelConstants.*;


/**
 *  A {@code FrameSequenceWorker} manages a {@code FileFrameSequence} and its
 *  A/V data workers in accordance to playback states. It can hold up to 127
 *  threads of {@code FileFrameLenses} and 127 audio tracks ({@code
 *  OutputSources}).
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 *
 *  @see        FileFrameSequence
 *  @see        FileFrameLens
 *  @see        OutputSource
 */
public class FrameSequenceWorker implements SequenceWorker {

//~~PUBLIC CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Default number of {@code FileFrameLenses} */
    public static final byte DEFAULT_LENSES = 3;


//~~PROTECTED CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** 9:59:59 (H:MM:SS) */
    protected static final short MAX_SECONDS = (60 * 60 * 9) - 1;


//~~OBJECT CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Parent Presenter to which status events are to be notified */
    protected final Presenter presenter;

    /** Path to working directory */
    protected final String path;

    /** Sequence on which this SequenceWorker works */
    protected final Sequence sequence;

    /** Sequence data readers and buffers (Lenses) */
    protected final List<FileFrameLens> lenses;

    /** EDENRenderer with which Frames are to be drawn */
    protected final MultiLensFrameRenderer renderer;

    /** JComponent to which Frames are to be drawn */
    protected final RendererComponent component;

    /** Sequence audio tracks */
    protected final List<OutputSource> tracks;

    /** A/V synchronization pilot */
    protected final OutputSource pilot;

    /** OutputMixer to which the audio tracks are to be mixed */
    protected final OutputMixer mixer;

    /** Self-adjusting {@code Timer} on which the EDENRenderer is to be run */
    protected final SyncroClock clockRender;

    /** Self-adjusting timer for everything else */
    protected final SimpleSyncroTimer clock;

    /** Threads on which the Lenses are to be run */
    protected final List<Thread> threadsLens;

    /** Thread on which the working OutputMixer is to be run */
    protected final Thread threadMixer;

    /** StringBuilder with which status event Strings are to be built */
    protected final StringBuilder stringMaker;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Makes a {@code FrameSequenceWorker} with the given {@code Presenter}
     *  and path to {@code Sequence} data
     */
    public FrameSequenceWorker(Presenter presenter, String path) throws
        IOException, MalformedSequenceException
    {
        this(presenter, path, DEFAULT_LENSES);
    }

    /**
     *  Makes a {@code FrameSequenceWorker} with the given {@code
     *  Presenter}, path to {@code Sequence} data, and number of {@code
     *  FileFrameLenses}.
     *
     *  @throws     IOException
     *              If a read operation fails or is interrupted
     *
     *  @throws     MalformedSequenceException
     *              If the {@code Sequence} definition is malformed
     */
    public FrameSequenceWorker(Presenter presenter, String path, byte lenses)
        throws IOException, MalformedSequenceException
    {
        this(presenter, path, lenses, false);
        initialize();
    }

    /**
     *  Subclasses may use this constructor to omit initialization, which
     *  involves their overriding update method, which may throw
     *  NullPointerException as some subclass-specific fields are yet to be
     *  initialized.
     */
    protected FrameSequenceWorker(Presenter presenter,
                                     String path,
                                       byte lenses,
                                    boolean dummy)
        throws IOException, MalformedSequenceException
    {
        FileFrameSequence sequence = makeSequence(path);
        this.presenter   = presenter == null ? new NullPresenter() : presenter;
        this.path        = path;
        this.sequence    = sequence;
        this.lenses      = makeLenses(path, sequence, lenses);

        this.renderer    = new MultiLensFrameRenderer(this.lenses,
            (double) sequence.getWidth() / sequence.getHeight()
        );
        this.component   = makeComponent();
        this.tracks      = makeTracks();
        this.pilot       = makePilot();
        this.mixer       = makeMixer();
        this.clockRender = makeClockRender();
        this.clock       = makeClock();
        this.threadMixer = makeDaemonThread(this.mixer, "/Mixer");
        this.threadsLens = makeLensThreads();
        this.stringMaker = new StringBuilder(TEXT_LENGTH);
    }

    /** To prevent uninitialized instantiations of this class */
    protected FrameSequenceWorker() {
        this.presenter   = null;
        this.path        = null;
        this.sequence    = null;
        this.tracks      = null;
        this.pilot       = null;
        this.mixer       = null;
        this.lenses      = null;
        this.renderer    = null;
        this.clockRender = null;
        this.clock       = null;
        this.component   = null;
        this.threadMixer = null;
        this.threadsLens = null;
        this.stringMaker = null;
    }


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Updates the working {@code Sequence} of this {@code SequenceWorker} */
    public void update() {
        int frame;
        boolean sync = false;

        if (this.clock.getCounter() == 0    &&
            this.sequence.getSkip() == PLAY &&
            this.pilot              != null)
        {
            frame = syncAV();
            sync = true;
        }
        else
        {
            frame = this.sequence.getPoint() + this.sequence.getSkip();
        }
        if (!this.sequence.setPoint(frame)) {
            updateOnBounds();
            pause(true);
            this.lenses.forEach(FileFrameLens::await);
            this.clockRender.tick();
        } else if (sync) {
            this.lenses.forEach(FileFrameLens::call);
        }
        if ((this.sequence.getSkip() == PLAY    &&
             this.clock.getCounter() %
            (this.sequence.getRate() / 2) == 0) ||
             this.sequence.getSkip() != PLAY)
        {
            this.presenter.call(null, makeMessage());
        }
    }

    /** @inheritDoc */
    @Override
    public void play() {
        syncVA();
        this.sequence.setSkip(PLAY);
        this.lenses.forEach(FileFrameLens::call);
        this.clockRender.start();
        this.clock.start();
        this.mixer.setHold(false);
        this.presenter.call(PLAY);
    }

    /** @inheritDoc */
    @Override
    public void pause() {
        pause(false);
    }

    /** @inheritDoc */
    @Override
    public void stop() {
        this.sequence.setSkip(PAUSE);
        jumpToStart();
        this.clockRender.stop();
        this.clock.stop();
        this.mixer.setHold(true);
        this.presenter.call(IDLE);
    }

    /** @inheritDoc */
    @Override
    public void fastRewind() {
        this.sequence.setSkip(FAST_REWIND);
        this.lenses.forEach(FileFrameLens::call);
        this.clockRender.start();
        this.clock.start();
        this.mixer.setHold(true);
        this.presenter.call(FAST_REWIND);
    }

    /** @inheritDoc */
    @Override
    public void fastForward() {
        this.sequence.setSkip(FAST_FORWARD);
        this.lenses.forEach(FileFrameLens::call);
        this.clockRender.start();
        this.clock.start();
        this.mixer.setHold(true);
        this.presenter.call(FAST_FORWARD);
    }

    /** @inheritDoc */
    @Override
    public void stepBackward() {
        if (this.sequence.getSkip() != 0 ||
           !this.sequence.setPoint(this.sequence.getPoint() - 1))
        {
            return;
        }
        this.lenses.forEach(FileFrameLens::call);
        this.lenses.forEach(FileFrameLens::await);
        this.clockRender.tick();
        this.clock.tick();
    }

    /** @inheritDoc */
    @Override
    public void stepForward() {
        if (this.sequence.getSkip() != PAUSE ||
           !this.sequence.setPoint(this.sequence.getPoint() + 1))
        {
            return;
        }
        this.lenses.forEach(FileFrameLens::call);
        this.lenses.forEach(FileFrameLens::await);
        this.clockRender.tick();
        this.clock.tick();
    }

    /** @inheritDoc */
    @Override
    public void jumpToStart() {
        jump(this.sequence.getStart());
    }

    /** @inheritDoc */
    @Override
    public void jumpToEnd() {
        jump(this.sequence.getEnd());
    }

    /** @inheritDoc */
    @Override
    public boolean jump(int point) {
        if (!this.sequence.setPoint(point)) {
            return false;
        }
        this.lenses.forEach(FileFrameLens::call);

        if (this.sequence.getSkip() == PAUSE) {
            this.lenses.forEach(FileFrameLens::await);
            this.clockRender.tick();
            this.clock.tick();
        }
        syncVA();
        return true;
    }

    /** @inheritDoc */
    @Override
    public void trickPlay() {
        this.sequence.setSkip(TRICKPLAY);
        this.lenses.forEach(FileFrameLens::call);
        this.clockRender.start();
        this.clock.start();
        this.mixer.setHold(true);
        this.presenter.call(TRICKPLAY);
    }

    /** @inheritDoc */
    @Override
    public void dismiss() {
        this.threadMixer.interrupt();
        this.threadsLens.forEach(Thread::interrupt);
        this.clockRender.stop();
        this.clock.end();
        this.mixer.close();

        this.tracks.stream()
            .filter(Objects::nonNull)
            .forEach(OutputSource::close);
        this.renderer.clearComponents();
    }


//~~~~ACCESSORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public String getPath() {
        return this.path;
    }

    /** @inheritDoc */
    @Override
    public Sequence getSequence() {
        return this.sequence;
    }

    /** @inheritDoc */
    @Override
    public RendererComponent getComponent() {
        return this.component;
    }

    /** @inheritDoc */
    @Override
    public float getAmplification() {
        return this.mixer.getDspData().getAmplification();
    }

    /** @inheritDoc */
    @Override
    public int getTrack() {
        return this.mixer.getSolo() + 1;
    }

    /** @inheritDoc */
    @Override
    public List<OutputSource> getTracks() {
        return new ArrayList<>(this.tracks);
    }


//~~~~MUTATORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public void setDrawStatistics(boolean drawStatistics) {
        this.renderer.setDrawStatistics(drawStatistics);
    }

    /** @inheritDoc */
    @Override
    public boolean toggleDrawStatistics() {
        return this.renderer.toggleDrawStatistics();
    }

    /** @inheritDoc */
    @Override
    public void setAmplification(float amplification) {
        this.mixer.getDspData().setAmplification(amplification);
    }

    /** @inheritDoc */
    @Override
    public void setMuted(boolean muted) {
        this.mixer.getDspData().setMuted(muted);
        this.presenter.call(muted ? MUTE : UNMUTE);
    }

    /** @inheritDoc */
    @Override
    public boolean toggleMuted() {
        boolean out = this.mixer.getDspData().toggleMuted();
        this.presenter.call(out ? MUTE : UNMUTE);
        return out;
    }

    /** @inheritDoc */
    @Override
    public void setTrack(int track) {
        this.mixer.setSolo((short) (track - 1));
    }


//~~~~PREDICATES~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public boolean isDrawStatistics() {
        return this.renderer.isDrawStatistics();
    }

    /** @inheritDoc */
    @Override
    public boolean isMuted() {
        return this.mixer.getDspData().isMuted();
    }


//~~PROTECTED OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Configures this SequenceWorker to not advance every update. The default
     *  pause operation involves synchronization with the state of the
     *  EDENRenderer. However, it does not work with updateOnBounds upon
     *  jumping. Hence the introduction of the boolean parameter.
     */
    protected void pause(boolean onBound) {
        this.sequence.setSkip(PAUSE);
        this.lenses.forEach(FileFrameLens::call);
        this.clockRender.pause();
        this.clock.pause();
        this.mixer.setHold(true);

        if (!onBound) {
            int frame = this.renderer.getFrame();

            if (frame != Integer.MIN_VALUE) {
                this.sequence.setPoint(frame);
            }
        }
        this.presenter.call(PAUSE);
    }

    /**
     *  Jumps to either the starting or ending point depending on which half the
     *  Sequence of this SequenceWorker is at
     */
    protected void updateOnBounds() {
        if (this.sequence.getElapsedPercent() < 0.5) {
            this.sequence.goToStart();
        } else {
            this.sequence.goToEnd();
        }
    }

    /** Performs A/V syncing with the pilot of this SequenceWorker */
    protected int syncAV() {
        return (int) Math.floor(this.pilot.getElapsedPercent() *
           (this.sequence.getLength() - 1)) +
            this.sequence.getStart();
    }

    /**
     *  Performs A/V syncing with the Sequence of this SequenceWorker
     *  as the pilot
     */
    protected void syncVA() {
        skipAudioTracks(this.sequence.getElapsedPercent());
    }

    /**
     *  Skips the OutputSources of this SequenceWorker to the given
     *  percentual point
     */
    protected void skipAudioTracks(double percent) {
        this.mixer.rewindAll();

        for (OutputSource s : this.tracks) {
            if (s == null) {
                continue;
            }
            long skip = Math.round(percent * s.getStreamSize());
            skip -= skip % this.pilot.getFormat().getFrameSize();
            s.skip(skip);
        }
    }

    /** Returns the time counter String to be displayed */
    protected String makeMessage() {
        double progress = Math.floor(this.sequence.getElapsedSecond());
        int seconds, minutes, hours;
        this.stringMaker.setLength(0);

        if (progress > MAX_SECONDS) {
            seconds = 59;
            minutes = 59;
            hours   = 9;
        } else {
            seconds = (int)            (progress % 60);
            minutes = (int) Math.floor((progress % 3600) / 60);
            hours   = (int) Math.floor((progress % MAX_SECONDS) / 3600);
        }
        this.stringMaker.append(hours);
        this.stringMaker.append(':');

        if (minutes < 10) {
            this.stringMaker.append('0');
        }
        this.stringMaker.append(minutes);
        this.stringMaker.append(':');

        if (seconds < 10) {
            this.stringMaker.append('0');
        }
        this.stringMaker.append(seconds);
        this.stringMaker.append(TEXT_BLANK);
        return this.stringMaker.toString();
    }


//~~~~CONSTRUCTOR HELPERS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Returns a FileFrameSequence with the parameters given by the metadata
     *  file in the directory pointed by the given path
     */
    private FileFrameSequence makeSequence(String path) throws
        IOException, MalformedSequenceException
    {
        Map<String, String> map = readMetadata(path);

        try {
            return new FileFrameSequence(map.get("name"),
               Integer.parseInt(map.get("start")),
               Integer.parseInt(map.get("end")),
                 Byte.parseByte(map.get("rate")),
               Short.parseShort(map.get("width")),
               Short.parseShort(map.get("height")),
                                map.get("extension")
            );
        } catch (NumberFormatException e) {
            throw new BadMetadataException();
        } catch (IllegalArgumentException e) {
            throw new BadParameterException();
        }
    }

    /**
     *  Returns the Map from parsing the file {@value ApplicationInstance#
     *  METADATA_FILE} in the directory pointed by the path of this
     *  SequenceWorker
     */
    private Map<String, String> readMetadata(String path) throws IOException {
        if (!Files.isDirectory(Paths.get(path))) {
            throw new NotDirectoryException(path);
        }
        String metadata = path + METADATA_FILE;

        if (!Files.exists(Paths.get(metadata))) {
            throw new NoSuchFileException(metadata);
        }
        return new ConfigFileReader(metadata).readToMap();
    }

    /**
     *  Returns a List of the given number of FileFrameLens, each with its own
     *  offsets
     */
    private List<FileFrameLens> makeLenses(String path,
                                FileFrameSequence sequence,
                                             byte size)
    {
        List<FileFrameLens> out = new ArrayList<>(size);

        for (byte b = 0; b < size; b++) {
            out.add(new FileFrameLens(path, sequence, b, (byte) (size - 1)));
        }
        return out;
    }

    /**
     *  Returns a RendererComponent with the MultiLensFrameRenderer of this
     *  SequenceWorker attached
     */
    private RendererComponent makeComponent() {
        RendererComponent out = new RendererComponent();
        out.addRenderer(this.renderer);
        return out;
    }

    /**
     *  Returns an immutable List of OutputSources from WAV files in the
     *  directory pointed by the path of this SequenceWorker
     */
    private List<OutputSource> makeTracks() throws IOException {
        List<OutputSource> out = new ArrayList<>();

        for (int channel = 1; channel <= Byte.MAX_VALUE; channel++) {
            String path = this.path + channel + ".wav";

            if (!Files.isRegularFile(Paths.get(path))) {
                out.add(null);
                continue;
            }
            File file = new File(path);

            try {
                out.add(new OutputSource(
                    new BufferedInputStream(
                        AudioSystem.getAudioInputStream(file)
                    ),  AudioSystem.getAudioFileFormat(file).getFormat()
                ));
            } catch (UnsupportedAudioFileException e) {
                // TODO
            }
        }
        return Collections.unmodifiableList(out);
    }

    /**
     *  Returns the first non-null OutputSource from the OutputSources of this
     *  SequenceWorker, or null if there are no OutputSources.
     */
    private OutputSource makePilot() {
        for (OutputSource s : this.tracks) {
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    /**
     *  Returns an OutputMixer with the OutputSources of this SequenceWorker
     *  attached
     */
    private OutputMixer makeMixer() {
        OutputMixer out;

        try {
            out = this.pilot != null ?
                new OutputMixer((byte)
                    this.tracks.size(), this.pilot.getFormat()
                ) :
                new OutputMixer((byte) 1);
        } catch (IllegalArgumentException e) {
            this.tracks.stream()
                .filter(Objects::nonNull)
                .forEach(OutputSource::close);

            throw e;
        }
        for (OutputSource s : this.tracks) {
            out.attach(s);
        }
        out.setSolo((short) 0);
        return out;
    }

    /**
     *  Returns a SyncroClock with its fire rate set to the rate given by the
     *  Sequence of this SequenceWorker
     */
    private SyncroClock makeClockRender() {
        SyncroClock out = new SyncroClock((short) this.sequence.getRate());
        out.addActionListener(this.component);
        return out;
    }

    /**
     *  Returns a SimpleSyncroTimer with its fire rate set to the rate
     *  given by the Sequence of this SequencedWorker
     */
    private SimpleSyncroTimer makeClock() {
        return new SimpleSyncroTimer(
            this::update, (short) this.sequence.getRate()
        );
    }

    /** Returns a new daemon Thread with the given Runnable and name */
    private Thread makeDaemonThread(Runnable target, String name) {
        Thread out = new Thread(target, name);
        out.setDaemon(true);
        return out;
    }

    /**
     *  Returns an immutable List of daemon Threads on which the working
     *  Lenses are to be run
     */
    private List<Thread> makeLensThreads() {
        List<Thread> out = new ArrayList<>(this.lenses.size());

        for (byte b = 0; b < this.lenses.size(); b++) {
            out.add(makeDaemonThread(this.lenses.get(b), "/Lens[" + b + "]"));
        }
        return Collections.unmodifiableList(out);
    }

    /** Initializes A/V workers and clocks for presentation */
    protected void initialize() {
        this.threadMixer.start();
        this.threadsLens.forEach(Thread::start);
        this.lenses.forEach(FileFrameLens::await);
        this.clockRender.tick();
        this.presenter.call(IDLE);
        this.presenter.call(this.sequence.getName(), makeMessage());
    }
}
