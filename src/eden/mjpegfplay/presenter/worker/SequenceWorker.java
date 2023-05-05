package eden.mjpegfplay.presenter.worker;

import eden.common.audio.OutputSource;
import eden.common.model.sequence.Sequence;
import eden.common.video.render.RendererComponent;
import eden.mjpegfplay.model.TransportConstants;
import java.util.List;

/**
 * A {@code SequenceWorker} manages a {@code Sequence} and its A/V data workers
 * in accordance to playback states.
 *
 * In the file system, a {@code Sequence} is a directory containing its metadata
 * file and A/V data.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 *
 * @see Sequence
 */
public interface SequenceWorker {
  /**
   * Configures this {@code SequenceWorker} to advance one point forward every
   * update
   */
  void play();
  /**
   * Configures this {@code SequenceWorker} to not advance every update
   */
  void pause();
  /**
   * Configures this {@code SequenceWorker} to not advance frames every update,
   * and rewinds to the starting point.
   */
  void stop();
  /**
   * Configures this {@code SequenceWorker} to advance {@value
   * TransportConstants#FAST_REWIND} frames backward every update
   */
  void fastRewind();
  /**
   * Configures this {@code SequenceWorker} to advance {@value
   * TransportConstants#FAST_FORWARD} frames forward every update
   */
  void fastForward();
  /**
   * Advances one point backward only if this {@code SequenceWorker} does not
   * advance frames every update
   */
  void stepBackward();
  /**
   * Advances one point forward only if this {@code SequenceWorker} does not
   * advance frames every update
   */
  void stepForward();
  /**
   * Jumps to the starting point of the {@code FileFrameSequence} of this
   * {@code SequenceWorker}
   */
  void jumpToStart();
  /**
   * Jumps to the ending point of the {@code FileFrameSequence} of this
   * {@code SequenceWorker}
   */
  void jumpToEnd();
  /**
   * Jumps to the given point of the {@code FileFrameSequence} of this {@code
   * SequenceWorker}
   *
   * @return {@code false} If an invalid point is passed;
   *
   * {@code true} If the operation is successful
   */
  boolean jump(int point);
  /**
   * Configures this {@code SequenceWorker} to advance one point backward every
   * update
   */
  void trickPlay();
  /**
   * Stops all activities and frees as much system resources allocated to this
   * {@code SequenceWorker} as possible
   */
  void dismiss();
  /** Returns the path to working directory of this {@code SequenceWorker} */
  String getPath();
  /**
   * Returns the working {@code FileFrameSequence} of this {@code
   * SequenceWorker}
   */
  Sequence getSequence();
  /**
   * Returns the working {@code RendererComponent} of this {@code
   * SequenceWorker}
   */
  RendererComponent getComponent();
  /**
   * Returns the amplification factor for the {@code OutputMixer} of this
   * {@code SequenceWorker}
   */
  float getAmplification();
  /** Returns the current output track */
  int getTrack();
  /**
   * Returns a {@code List} of all {@code OutputSources} of this {@code
   * SequenceWorker}. The {@code List} may contain {@code null} to denote the
   * absence of an audio track associated to the track number given by the
   * index. The actual type to be returned is an {@code ArrayList}.
   */
  List<OutputSource> getTracks();
  /** Sets whether render statistics are to be drawn */
  void setDrawStatistics(boolean drawStatistics);
  /**
   * Toggles whether render statistics are to be rendered and returns its new
   * value
   */
  boolean toggleDrawStatistics();
  /**
   * Sets the amplification factor for the {@code OutputMixer} of this {@code
   * SequenceWorker}
   */
  void setAmplification(float amplification);
  /**
   * Sets whether the {@code OutputMixer} of this {@code SequenceWorker} is
   * muted
   */
  void setMuted(boolean muted);
  /**
   * Toggles whether the {@code OutputMixer} of this {@code SequenceWorker} is
   * muted ans returns its new value
   */
  boolean toggleMuted();
  /**
   * Configures the {@code OutputMixer} of this {@code SequenceWorker} to output
   * the given audio track
   *
   * @param track Sole channel to output. Zero or negative values cause all
   * tracks to mix altogether.
   */
  void setTrack(int track);
  /** Returns whether render statistics are to be drawn */
  boolean isDrawStatistics();
  /**
   * Returns whether the {@code OutputMixer} of this {@code SequenceWorker} is
   * muted
   */
  boolean isMuted();
}
