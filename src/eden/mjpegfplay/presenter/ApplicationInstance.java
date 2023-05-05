package eden.mjpegfplay.presenter;

import static eden.common.shared.Constants.NUL_STRING;
import static eden.mjpegfplay.model.ApplicationInformation.*;
import static eden.mjpegfplay.model.SequenceTypes.*;
import static eden.mjpegfplay.model.TransportConstants.*;

import eden.common.io.ConfigFileReader;
import eden.common.io.ConfigFileWriter;
import eden.common.io.MappedFileReader;
import eden.common.io.MappedFileWriter;
import eden.mjpegfplay.presenter.worker.FrameSequenceWorker;
import eden.mjpegfplay.presenter.worker.FreezingFrameSequenceWorker;
import eden.mjpegfplay.presenter.worker.MusicPlaylistWorker;
import eden.mjpegfplay.presenter.worker.SequenceWorker;
import eden.mjpegfplay.view.ApplicationUI;
import eden.mjpegfplay.view.ApplicationUIMaker;
import eden.mjpegfplay.view.FrontPanelInterface;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@code ApplicationInstance} represents an instance of this application. It
 * manages retrievals, presentations, modifications, and storage of end user
 * data, and provides a high-level interface to the application workers.
 *
 * As such, some method documentations for this class are intended for the end
 * user.
 *
 * This class represents the presenter in the model-view-presenter architectural
 * pattern of this application.
 *
 * @author Brendon
 * @version u0r5, 05/05/2023.
 */
public class ApplicationInstance implements Presenter {

  /** Metadata filename */
  public static final String METADATA_FILE = "metadata.edencfg";
  /** Application worker */
  private SequenceWorker worker = null;
  /** Application user interface */
  private final ApplicationUI ui = new ApplicationUIMaker(this);
  /** Status display panel */
  private final FrontPanelInterface panel = this.ui.getFrontPanel();
  /** EDEN configuration file reader */
  private final MappedFileReader reader = new ConfigFileReader();
  /** EDEN configuration file writer */
  private final MappedFileWriter writer = new ConfigFileWriter();
  /** Current sequence data */
  private Map<String, String> map = new HashMap<>();
  /** Type of application worker */
  private String type = null;
  /**
   * Indicates whether changes to the currently loaded sequence are not yet
   * saved to non-volatile memory
   */
  private boolean modified = false;
  /** Volume level */
  private float amplification = 1.0f;
  /** Indicates whether the audio output is muted */
  private boolean muted = false;
  /** Indicates whether render statistics are to be drawn */
  private boolean drawStatistics = false;
  /** Count of {@code FileFrameLenses} */
  private byte lensCount = FrameSequenceWorker.DEFAULT_LENSES;

  /** Makes a new instance of this application */
  public ApplicationInstance() {
    initialize();
  }

  /** Makes a new sequence with the given properties */
  public void make(
    String path,
    String name,
    int start,
    int end,
    int rate,
    short width,
    short height
  ) throws Exception {
    this.panel.setWait(true);
    this.panel.call();
    setName(name);
    setStart(start);
    setEnd(end);
    setRate(rate);
    setWidth(width);
    setHeight(height);
    this.map.put("extension", "jpg");
    this.writer.setPath(path + METADATA_FILE);
    this.writer.write(this.map);
    this.panel.setWait(false);
    this.panel.call();
  }

  /** Opens the sequence directory pointed by the given path */
  public void open(String path, String type) throws Exception {
    this.panel.setWait(true);
    this.panel.paint();
    SequenceWorker worker = this.worker;
    try {
      this.worker = makeWorker(path, type);
      setUserData();
      if (worker != null) {
        worker.dismiss();
        this.ui.returnToStandby();
      }
      this.reader.setPath(this.worker.getPath() + METADATA_FILE);
      this.writer.setPath(this.worker.getPath() + METADATA_FILE);
      this.map = reader.readToMap();
      this.type = type;
      this.modified = false;
    } catch (Exception e) {
      this.panel.setWait(false);
      this.panel.call();
      throw e;
    }
    this.ui.initializePresentation(
        this.map.get("name"),
        this.worker.getComponent()
      );
    this.panel.setWait(false);
    this.panel.call();
  }

  /** Saves the current metadata into the appropriate file */
  public void save() throws Exception {
    this.panel.setWait(true);
    this.panel.call();
    try {
      this.writer.write(this.map);
      this.modified = false;
    } catch (Exception e) {
      this.panel.setWait(false);
      this.panel.call();
      throw e;
    }
    this.panel.setWait(false);
    this.panel.call();
  }

  /** Reloads the currently loaded sequence */
  public void reload() throws Exception {
    String path = this.worker.getPath();
    close();
    open(path, this.type);
  }

  /** Closes the currently loaded sequence */
  public void close() {
    this.panel.setWait(true);
    this.panel.call();
    if (this.worker != null) {
      this.worker.dismiss();
      this.worker = null;
    }
    this.map.clear();
    this.modified = false;
    this.ui.returnToStandby();
    this.panel.setFromMode(CLOSE);
    this.panel.setText0(NUL_STRING);
    this.panel.setText1("Sequence Closed");
    this.panel.setWait(false);
    this.panel.call();
  }

  /** {@inheritDoc} */
  @Override
  public void call(int event) {
    this.panel.setFromMode(event);
    this.panel.call();
  }

  /** {@inheritDoc} */
  @Override
  public void call(String message0, String message1) {
    this.panel.setTexts(message0, message1);
    this.panel.call();
  }

  /** Starts/resumes sequence playback */
  public void play() {
    this.worker.play();
  }

  /** Pauses sequence playback */
  public void pause() {
    this.worker.pause();
  }

  /** Ends sequence playback */
  public void stop() {
    this.worker.stop();
  }

  /** Quickly advances towards the start of the sequence */
  public void fastRewind() {
    this.worker.fastRewind();
  }

  /** Quickly advances towards the end of the sequence */
  public void fastForward() {
    this.worker.fastForward();
  }

  /** Advances one frame towards the start of the sequence */
  public void stepBackward() {
    this.worker.stepBackward();
  }

  /** Advances one frame towards the end of the sequence */
  public void stepForward() {
    this.worker.stepForward();
  }

  /** Jumps to the start of the sequence */
  public void jumpToStart() {
    this.worker.jumpToStart();
  }

  /** Jumps to the end of the sequence */
  public void jumpToEnd() {
    this.worker.jumpToEnd();
  }

  /** Jumps to the given frame within the sequence */
  public boolean jump(int frame) {
    return this.worker.jump(frame);
  }

  /** Starts/resumes reversed sequence playback */
  public void trickPlay() {
    this.worker.trickPlay();
  }

  /** Returns the name of the currently loaded sequence */
  public String getName() {
    return this.map.get("name");
  }

  /** Returns the starting frame of the currently loaded sequence */
  public int getStart() {
    return Integer.parseInt(this.map.get("start"));
  }

  /** Returns the ending frame of the currently loaded sequence */
  public int getEnd() {
    return Integer.parseInt(this.map.get("end"));
  }

  /**
   * Returns the number of frames to render per second for the currently loaded
   * sequence
   */
  public byte getRate() {
    return Byte.parseByte(this.map.get("rate"));
  }

  /** Returns the current frame of the currently loaded sequence */
  public int getPoint() {
    return this.worker.getSequence().getPoint();
  }

  /**
   * Returns the number of frames to advance for the currently loaded sequence
   */
  public int getSkip() {
    return this.worker.getSequence().getSkip();
  }

  /**
   * Returns the length of the currently loaded sequence in number of frames
   */
  public int getLength() {
    return this.worker.getSequence().getLength();
  }

  /** Returns the length of the currently loaded sequence in seconds */
  public double getLengthSecond() {
    return this.worker.getSequence().getLengthSecond();
  }

  /**
   * Returns the number of elapsed frames for the currently loaded sequence
   */
  public int getElapsedFrames() {
    return this.worker.getSequence().getElapsed();
  }

  /** Returns the elapsed time for the currently loaded sequence in seconds */
  public double getElapsedSecond() {
    return this.worker.getSequence().getElapsedSecond();
  }

  /** Returns the percentual progress for the currently loaded sequence */
  public double getElapsedPercent() {
    return this.worker.getSequence().getElapsedPercent();
  }

  /** Returns the audio output level */
  public float getAmplification() {
    return this.worker == null
      ? this.amplification
      : this.worker.getAmplification();
  }

  /** Returns the audio track to output during playback */
  public int getTrack() {
    return this.worker.getTrack();
  }

  /** Sets the name of the currently loaded sequence */
  public void setName(String name) {
    this.map.put("name", name);
    this.modified = true;
  }

  /** Sets the starting frame of the currently loaded sequence */
  public void setStart(int point) {
    this.map.put("start", Integer.toString(point));
    this.modified = true;
  }

  /** Sets the ending frame of the currently loaded sequence */
  public void setEnd(int point) {
    this.map.put("end", Integer.toString(point));
    this.modified = true;
  }

  /**
   * Sets the number of frames to render per second for the currently loaded
   * sequence
   */
  public void setRate(int rate) {
    this.map.put("rate", Integer.toString(rate));
    this.modified = true;
  }

  /** Sets the projection width of the currently loaded sequence in pixels */
  public void setWidth(short width) {
    this.map.put("width", Short.toString(width));
    this.modified = true;
  }

  /** Sets the projection height of the currently loaded sequence in pixels */
  public void setHeight(short height) {
    this.map.put("height", Short.toString(height));
    this.modified = true;
  }

  /** Sets whether render statistics are to be drawn */
  public void setDrawStatistics(boolean drawStatistics) {
    if (this.worker == null) {
      this.drawStatistics = drawStatistics;
      return;
    }
    this.worker.setDrawStatistics(drawStatistics);
    this.drawStatistics = this.worker.isDrawStatistics();
  }

  /**
   * Toggles whether render statistics are to be drawn and returns its new value
   */
  public boolean toggleDrawStatistics() {
    if (this.worker == null) {
      this.drawStatistics = !this.drawStatistics;
      return this.drawStatistics;
    }
    boolean out = this.worker.toggleDrawStatistics();
    this.drawStatistics = out;
    return out;
  }

  /** Sets its count of {@code FileFrameLenses}. */
  public void setLensCount(byte count) {
    this.lensCount = count;
  }

  /** Adjusts the audio output level */
  public void setAmplification(float amplification) {
    if (this.worker == null) {
      this.amplification = amplification;
      return;
    }
    this.worker.setAmplification(amplification);
    this.amplification = this.worker.getAmplification();
  }

  /** (Un)mutes audio output */
  public void setMuted(boolean muted) {
    if (this.worker == null) {
      this.muted = muted;
      this.panel.setMute(this.muted);
      this.panel.call();
      return;
    }
    this.worker.setMuted(muted);
    this.muted = this.worker.isMuted();
  }

  /** (Un)mutes audio output and returns its new value */
  public boolean toggleMuted() {
    if (this.worker == null) {
      this.muted = !this.muted;
      this.panel.setMute(this.muted);
      this.panel.call();
      return this.muted;
    }
    boolean out = this.worker.toggleMuted();
    this.muted = out;
    return out;
  }

  /** Changes the audio track to output during playback */
  public void setTrack(int track) {
    this.worker.setTrack(track);
  }

  /**
   * Returns whether changes to the currently loaded sequence are not yet saved
   * to non-volatile memory
   */
  public boolean isModified() {
    return this.modified;
  }

  /** Returns whether any Sequence is loaded to memory */
  public boolean isLoaded() {
    return this.worker != null;
  }

  /** Returns whether render statistics are to be drawn */
  public boolean isDrawStatistics() {
    return this.worker == null
      ? this.drawStatistics
      : this.worker.isDrawStatistics();
  }

  /** Returns whether the audio output is muted */
  public boolean isMuted() {
    return this.worker == null ? this.muted : this.worker.isMuted();
  }

  /** Returns a new SequenceWorker of the given type with the given path */
  private SequenceWorker makeWorker(String path, String type) throws Exception {
    SequenceWorker out = null;
    switch (type) {
      case SEQUENCE:
        out = new FrameSequenceWorker(this, path, this.lensCount);
        this.panel.setHighSpeed(true);
        break;
      case FREEZING_SEQUENCE:
        out = new FreezingFrameSequenceWorker(this, path);
        this.panel.setHighSpeed(false);
        break;
      case MUSIC_SEQUENCE:
        out = new MusicPlaylistWorker(this, path);
        this.panel.setHighSpeed(false);
    }
    return out;
  }

  /** Passes user data into the current SequenceWorker */
  private void setUserData() {
    this.worker.setAmplification(this.amplification);
    this.worker.setMuted(this.muted);
    this.worker.setDrawStatistics(this.drawStatistics);
  }

  /** Initializes the front display panel */
  private void initialize() {
    new Thread(this.ui.getFrontPanel()).start();
    this.panel.setText0(APPLICATION_NAME + "-" + APPLICATION_VERSION);
    this.panel.setText1("NO SEQUENCE");
    this.panel.call();
  }
}
