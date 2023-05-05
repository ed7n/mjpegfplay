package eden.mjpegfplay.presenter.worker;

import static eden.mjpegfplay.model.TransportConstants.*;
import static eden.mjpegfplay.presenter.ApplicationInstance.METADATA_FILE;
import static eden.mjpegfplay.view.FrontPanelConstants.*;

import eden.common.audio.OutputMixer;
import eden.common.audio.OutputSource;
import eden.common.clock.SimpleSyncroTimer;
import eden.common.io.ConfigFileReader;
import eden.common.model.sequence.Sequence;
import eden.common.video.render.RendererComponent;
import eden.mjpegfplay.presenter.Presenter;
import eden.mjpegfplay.presenter.exception.BadMetadataException;
import eden.mjpegfplay.presenter.exception.BadParameterException;
import eden.mjpegfplay.presenter.exception.MalformedSequenceException;
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
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A {@code MusicPlaylistWorker} plays audio tracks sequentially rather than
 * concurrently.
 *
 * @author Brendon
 * @version u0r4, 11/06/2021.
 */
public class MusicPlaylistWorker implements SequenceWorker {

  /** 99:59 (H:MM:SS) */
  private static final short MAX_SECONDS = (100 * 60) - 1;
  /** Parent Presenter to which status events are to be notified */
  private final Presenter presenter;
  /** Path to working directory */
  private final String path;
  /** Sequence on which this SequenceWorker works */
  private final Sequence sequence;
  /** Sequence audio tracks */
  private final List<OutputSource> tracks;
  /** OutputMixer to which the audio tracks are to be mixed */
  private final OutputMixer mixer;
  /** Self-adjusting timer for everything else */
  private final SimpleSyncroTimer clock;
  /** Thread on which the working OutputMixer is to be run */
  private final Thread threadMixer;
  /** StringBuilder with which status event Strings are to be built */
  private final StringBuilder stringMaker;
  /** Current audio track being selected */
  private OutputSource track;
  /** Next channel on which an OutputSource is to be attached */
  private byte channel;

  /**
   * Makes a {@code MusicPlaylistWorker} with the given {@code Presenter} and
   * path to {@code Sequence} data
   */
  public MusicPlaylistWorker(Presenter presenter, String path)
    throws IOException, MalformedSequenceException {
    this.presenter = presenter;
    this.path = path;
    this.sequence = makeSequence(path);
    this.tracks = makeTracks();
    this.mixer = makeMixer();
    this.clock = makeClock();
    this.threadMixer = makeDaemonThread(this.mixer, "/Mixer");
    this.stringMaker = new StringBuilder(TEXT_LENGTH);
    this.track = this.mixer.getChannel(0);
    this.channel = 1;
    initialize();
  }

  /**
   * Updates the working audio container and workers of this {@code
   * SequenceWorker}
   */
  public void update() {
    if (this.track != null && this.track.isDone()) {
      if (this.sequence.getPoint() >= this.sequence.getEnd()) {
        stop();
        this.presenter.call(null, " END");
      } else {
        stepForward();
      }
      return;
    }
    if (this.clock.getCounter() % 25 == 0 && this.sequence.getSkip() == PLAY) {
      this.presenter.call(null, makeMessage());
      return;
    } else if (this.sequence.getSkip() == FAST_FORWARD) {
      this.track.skip(
          this.track.getFormat().getFrameSize() * 4800 * FAST_FORWARD
        );
    } else if (this.sequence.getSkip() == FAST_REWIND) {
      int position = this.track.getPosition();
      if (position == 0) {
        if (this.sequence.getPoint() == this.sequence.getStart()) {
          pause();
        } else {
          stepBackward();
          this.track.skip(
              this.track.getStreamSize() +
              (this.track.getFormat().getFrameSize() * 4800 * FAST_REWIND)
            );
        }
        return;
      }
      this.track.jumpToStart();
      this.track.skip(
          position +
          (this.track.getFormat().getFrameSize() * 4800 * FAST_REWIND)
        );
    }
    this.presenter.call(null, makeMessage());
  }

  /** {@inheritDoc} */
  @Override
  public void play() {
    this.sequence.setSkip(PLAY);
    this.mixer.setHold(false);
    this.clock.start();
    this.presenter.call(PLAY);
  }

  /** {@inheritDoc} */
  @Override
  public void pause() {
    this.sequence.setSkip(PAUSE);
    this.mixer.setHold(true);
    this.clock.pause();
    this.presenter.call(PAUSE);
  }

  /** {@inheritDoc} */
  @Override
  public void stop() {
    this.sequence.setSkip(PAUSE);
    this.mixer.setHold(true);
    this.mixer.rewindAll();
    this.clock.pause();
    this.presenter.call(IDLE);
    jump(this.sequence.getStart());
  }

  /** {@inheritDoc} */
  @Override
  public void fastRewind() {
    this.sequence.setSkip(FAST_REWIND);
    this.clock.start();
    this.presenter.call(FAST_REWIND);
  }

  /** {@inheritDoc} */
  @Override
  public void fastForward() {
    this.sequence.setSkip(FAST_FORWARD);
    this.clock.start();
    this.presenter.call(FAST_FORWARD);
  }

  /** {@inheritDoc} */
  @Override
  public void stepBackward() {
    jump(this.sequence.getPoint() - 1);
  }

  /** {@inheritDoc} */
  @Override
  public void stepForward() {
    jump(this.sequence.getPoint() + 1);
  }

  /** {@inheritDoc} */
  @Override
  public void jumpToStart() {
    this.mixer.rewindAll();
    this.presenter.call(null, makeMessage());
  }

  /** {@inheritDoc} */
  @Override
  public void jumpToEnd() {
    stepForward();
  }

  /** {@inheritDoc} */
  @Override
  public boolean jump(int point) {
    if (point == this.sequence.getPoint()) {
      this.presenter.call(null, makeMessage());
      return true;
    }
    if (!this.sequence.setPoint(point)) {
      return false;
    }
    this.track = this.tracks.get(point - 1);
    this.mixer.attach(this.track, this.channel);
    this.mixer.setSolo(this.channel);
    this.mixer.rewindAll();
    this.channel = (byte) ((this.channel + 1) % 3);
    this.mixer.detach(this.channel);
    this.presenter.call(null, makeMessage());
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public void trickPlay() {}

  /** {@inheritDoc} */
  @Override
  public void dismiss() {
    this.threadMixer.interrupt();
    this.clock.end();
    this.mixer.close();
    this.tracks.stream().filter(Objects::nonNull).forEach(OutputSource::close);
  }

  /** {@inheritDoc} */
  @Override
  public String getPath() {
    return this.path;
  }

  /** {@inheritDoc} */
  @Override
  public Sequence getSequence() {
    return this.sequence;
  }

  /** {@inheritDoc} */
  @Override
  public RendererComponent getComponent() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public float getAmplification() {
    return this.mixer.getDspData().getAmplification();
  }

  /** {@inheritDoc} */
  @Override
  public int getTrack() {
    return 1;
  }

  /** {@inheritDoc} */
  @Override
  public List<OutputSource> getTracks() {
    return this.tracks;
  }

  /** {@inheritDoc} */
  @Override
  public void setDrawStatistics(boolean drawStatistics) {}

  /** {@inheritDoc} */
  @Override
  public boolean toggleDrawStatistics() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public void setAmplification(float amplification) {
    this.mixer.getDspData().setAmplification(amplification);
  }

  /** {@inheritDoc} */
  @Override
  public void setMuted(boolean muted) {
    this.mixer.setMuted(muted);
    this.presenter.call(muted ? MUTE : UNMUTE);
  }

  /** {@inheritDoc} */
  @Override
  public boolean toggleMuted() {
    boolean out = this.mixer.toggleMuted();
    this.presenter.call(out ? MUTE : UNMUTE);
    return out;
  }

  /** {@inheritDoc} */
  @Override
  public void setTrack(int track) {}

  /** {@inheritDoc} */
  @Override
  public boolean isDrawStatistics() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMuted() {
    return this.mixer.getDspData().isMuted();
  }

  /** Returns the time counter String to be displayed */
  private String makeMessage() {
    this.stringMaker.setLength(0);
    this.stringMaker.append("TRACK  ");
    this.stringMaker.append(this.sequence.getPoint());
    this.stringMaker.append("  ");
    if (this.sequence.getPoint() < 10) {
      this.stringMaker.append(' ');
    }
    if (this.track == null) {
      this.stringMaker.append(" E:RR");
      return this.stringMaker.toString();
    }
    double progress = Math.floor(this.track.getElapsedSecond());
    int seconds, minutes;
    if (progress > MAX_SECONDS) {
      seconds = 59;
      minutes = 99;
    } else {
      seconds = (int) (progress % 60);
      minutes = (int) Math.floor((progress % 3600) / 60);
    }
    if (minutes < 10) {
      this.stringMaker.append(' ');
    }
    this.stringMaker.append(minutes);
    this.stringMaker.append(':');
    if (seconds < 10) {
      this.stringMaker.append('0');
    }
    this.stringMaker.append(seconds);
    this.stringMaker.append(' ');
    return this.stringMaker.toString();
  }

  /**
   * Returns a Sequence with the parameters given by the metadata file in the
   * directory pointed by the given path
   */
  private Sequence makeSequence(String path)
    throws IOException, MalformedSequenceException {
    Map<String, String> map = readMetadata(path);
    try {
      return new Sequence(
        map.get("name"),
        Integer.parseInt(map.get("start")),
        Integer.parseInt(map.get("end")),
        Byte.parseByte(map.get("rate"))
      );
    } catch (NumberFormatException e) {
      throw new BadMetadataException();
    } catch (IllegalArgumentException e) {
      throw new BadParameterException();
    }
  }

  /**
   * Returns the Map from parsing the file {@value ApplicationInstance#
   * METADATA_FILE} in the directory pointed by the path of this SequenceWorker
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
   * Returns an immutable List of OutputSources from WAV files in the directory
   * pointed by the path of this SequenceWorker
   */
  private List<OutputSource> makeTracks() throws IOException {
    List<OutputSource> out = new ArrayList<>();
    for (
      int channel = this.sequence.getPoint();
      channel <= this.sequence.getEnd();
      channel++
    ) {
      String path = this.path + channel + ".wav";
      if (!Files.isRegularFile(Paths.get(path))) {
        out.add(null);
        continue;
      }
      File file = new File(path);
      try {
        out.add(new OutputSource(file));
      } catch (UnsupportedAudioFileException e) {
        // TODO
      }
    }
    return Collections.unmodifiableList(out);
  }

  /**
   * Returns an OutputMixer with the OutputSources of this SequenceWorker
   * attached
   */
  private OutputMixer makeMixer() {
    OutputSource source = null;
    for (OutputSource s : this.tracks) {
      if (s != null) {
        source = s;
        break;
      }
      if (!this.sequence.setPoint(this.sequence.getPoint() + 1)) {
        break;
      }
    }
    OutputMixer out = source == null
      ? new OutputMixer((byte) 3)
      : new OutputMixer((byte) 3, source.getFormat(), 9600);
    out.attach(source);
    out.setSolo((short) 0);
    return out;
  }

  /**
   * Returns a SimpleSyncroTimer that handles end-of-track detection and panel
   * display updates
   */
  private SimpleSyncroTimer makeClock() {
    return new SimpleSyncroTimer(this::update, (short) 50);
  }

  /** Returns a new daemon Thread with the given Runnable and name */
  private Thread makeDaemonThread(Runnable target, String name) {
    Thread out = new Thread(target, name);
    out.setDaemon(true);
    return out;
  }

  /** Initializes A/V workers and clocks for presentation */
  private void initialize() {
    this.threadMixer.start();
    this.clock.start();
    this.presenter.call(IDLE);
    this.presenter.call(this.sequence.getName(), makeMessage());
  }
}
