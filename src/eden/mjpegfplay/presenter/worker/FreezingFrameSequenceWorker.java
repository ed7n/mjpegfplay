package eden.mjpegfplay.presenter.worker;

import eden.mjpegfplay.presenter.Presenter;
import eden.mjpegfplay.presenter.exception.BadFreezePointException;
import eden.mjpegfplay.presenter.exception.BadMetadataException;
import eden.mjpegfplay.presenter.exception.MalformedSequenceException;

import eden.common.io.ConfigFileReader;
import eden.common.io.active.FileFrameLens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static eden.mjpegfplay.model.TransportConstants.*;
import static eden.mjpegfplay.presenter.ApplicationInstance.METADATA_FILE;
import static eden.mjpegfplay.view.FrontPanelConstants.*;

/**
 * A {@code FreezingFrameSequenceWorker} manages a freezing {@code
 * FileFrameSequence} and its A/V data workers in accordance to playback states.
 * <p>
 * A freezing {@code FileFrameSequence} contains parts in which certain frames
 * span longer durations, omitting any frames in between freezing intervals.
 * This allows {@code FileFrameSequences} to achieve better overall compression
 * rations, although requiring more time and effort to be made.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
public class FreezingFrameSequenceWorker extends FrameSequenceWorker implements
    Runnable {

  /** Immutable List of freezing intervals */
  private final List<Integer> freezePoints;

  /**
   * Indicates whether this FreezingFrameSequenceWorker is freezing its Sequence
   */
  private AtomicBoolean freezing;

  /** Actual current frame that is not affected by freezing */
  private int position;

  /** Starting point of the current freezing interval pointed by an int */
  private int index;

  /**
   * Actual number of frames to skip per render that is not affected by freezing
   */
  private int skip;

  /**
   * Makes a {@code FreezingFrameSequenceWorker} with the given {@code
   * Presenter} and path to {@code Sequence} data
   *
   * @throws IOException If a read operation fails or is interrupted
   *
   * @throws MalformedSequenceException If the {@code Sequence} definition is
   * malformed
   */
  public FreezingFrameSequenceWorker(Presenter instance, String path)
      throws IOException, MalformedSequenceException {
    super(instance, path, (byte) 1, true);
    this.freezePoints = makeFreezePoints();
    this.freezing = new AtomicBoolean(false);
    this.position = this.sequence.getStart();
    this.index = 0;
    initialize();
  }

  /** To prevent uninitialized instantiations of this class */
  private FreezingFrameSequenceWorker() {
    this.freezePoints = null;
  }

  /** Runs this {@code FrontPanel} */
  public void run() {
    update();
    this.presenter.call(null, makeMessage());
  }

  /** {@inheritDoc} */
  @Override
  public void update() {
    int position;
    boolean sync = false;

    if (this.clock.getCounter() == 0 && this.sequence.getSkip() == PLAY
        && this.pilot != null) {
      position = syncAV();
      sync = true;
    } else
      position = this.position + this.skip;
    updateSpecial(position, sync);
  }

  /** {@inheritDoc} */
  @Override
  public void play() {
    syncVA();
    this.skip = PLAY;

    if (!this.freezing.get()) {
      this.sequence.setSkip(PLAY);
      this.lenses.forEach(FileFrameLens::call);
    }
    this.clockRender.start();
    this.clock.start();
    this.mixer.setHold(false);
    this.presenter.call(PLAY);
  }

  /** {@inheritDoc} */
  @Override
  public void pause() {
    pause(false);
  }

  /** {@inheritDoc} */
  @Override
  public void stop() {
    this.freezing.set(false);
    this.skip = 0;
    super.stop();
  }

  /** {@inheritDoc} */
  @Override
  public void fastRewind() {
    this.skip = FAST_REWIND;

    if (!this.freezing.get()) {
      this.sequence.setSkip(FAST_REWIND);
      this.lenses.forEach(FileFrameLens::call);
    }
    this.clockRender.start();
    this.clock.start();
    this.mixer.setHold(true);
    this.presenter.call(FAST_REWIND);
  }

  /** {@inheritDoc} */
  @Override
  public void fastForward() {
    this.skip = FAST_FORWARD;

    if (!this.freezing.get()) {
      this.sequence.setSkip(FAST_FORWARD);
      this.lenses.forEach(FileFrameLens::call);
    }
    this.clockRender.start();
    this.clock.start();
    this.mixer.setHold(true);
    this.presenter.call(FAST_FORWARD);
  }

  /** {@inheritDoc} */
  @Override
  public void stepBackward() {
    if (this.skip != PAUSE)
      return;
    this.position--;

    if (!this.freezing.get()) {
      this.sequence.setPoint(this.sequence.getPoint() - 1);
      this.lenses.forEach(FileFrameLens::call);
      this.lenses.forEach(FileFrameLens::await);
    }
    this.clockRender.tick();
    this.clock.tick();
  }

  /** {@inheritDoc} */
  @Override
  public void stepForward() {
    if (this.skip != PAUSE)
      return;
    this.position++;

    if (!this.freezing.get()) {
      this.sequence.setPoint(this.sequence.getPoint() + 1);
      this.lenses.forEach(FileFrameLens::call);
      this.lenses.forEach(FileFrameLens::await);
    }
    this.clockRender.tick();
    this.clock.tick();
  }

  /** {@inheritDoc} */
  @Override
  public void jumpToStart() {
    this.sequence.setSkip(this.skip);
    this.freezing.set(false);
    this.position = this.sequence.getStart();
    jump(this.sequence.getStart());
  }

  /** {@inheritDoc} */
  @Override
  public void jumpToEnd() {
    this.sequence.setSkip(this.skip);
    this.freezing.set(false);
    this.position = this.sequence.getEnd();
    jump(this.sequence.getEnd());
  }

  /** {@inheritDoc} */
  @Override
  public boolean jump(int position) {
    if (!this.sequence.isValidPoint(position))
      return false;
    updateSpecial(position, true);
    syncVA();

    if (this.skip == 0) {
      this.lenses.forEach(FileFrameLens::await);
      this.clockRender.tick();
      this.clock.tick();
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public void trickPlay() {
    this.skip = TRICKPLAY;

    if (!this.freezing.get()) {
      this.sequence.setSkip(TRICKPLAY);
      this.lenses.forEach(FileFrameLens::call);
    }
    this.clockRender.start();
    this.clock.start();
    this.mixer.setHold(true);
    this.presenter.call(TRICKPLAY);
  }

  /** {@inheritDoc} */
  @Override
  protected void pause(boolean onBound) {
    this.skip = PAUSE;

    if (!this.freezing.get()) {
      this.sequence.setSkip(PAUSE);
      this.lenses.forEach(FileFrameLens::call);
    }
    this.clockRender.pause();
    this.clock.pause();
    this.mixer.setHold(true);

    if (!onBound && !this.freezing.get()) {
      int frame = this.renderer.getFrame();

      if (frame != Integer.MIN_VALUE) {
        this.position = frame;
        this.sequence.setPoint(frame);
      }
    }
    this.presenter.call(PAUSE);
  }

  /** {@inheritDoc} */
  @Override
  protected void syncVA() {
    skipAudioTracks((double) (this.position - this.sequence.getStart())
        / (this.sequence.getLength() - 1));
  }

  /** {@inheritDoc} */
  @Override
  protected String makeMessage() {
    this.stringMaker.setLength(0);

    if (this.freezing.get())
      this.stringMaker.append('.');
    else
      this.stringMaker.append(' ');
    this.stringMaker.append(this.position - this.skip);
    this.stringMaker.append(TEXT_BLANK);
    return this.stringMaker.toString();
  }

  /** FreezingFrameSequenceWorker-specific extension to update */
  private void updateSpecial(int position, boolean sync) {
    int frame = getNextFrame(position);

    if (position != frame && this.freezing.compareAndSet(false, true)) {
      this.sequence.setSkip(PAUSE);
      sync = true;
    } else if (position == frame && this.freezing.compareAndSet(true, false)) {
      this.sequence.setSkip(this.skip);
      sync = true;
    }
    if (!this.sequence.setPoint(frame)) {
      updateOnBounds();
      this.position = this.sequence.getPoint();
      pause(true);
      this.lenses.forEach(FileFrameLens::await);
      this.clock.tick();
    } else if (sync) {
      this.lenses.forEach(FileFrameLens::call);
      this.position = position;
    } else
      this.position = position;
    this.presenter.call(null, makeMessage());
  }

  /** Returns the frame affected by freezing based on the given position */
  private int getNextFrame(int position) {
    int difference = position - this.position;

    if (difference == 0)
      return position;
    if (this.position == this.sequence.getStart()) {
      this.index = 0;
      return this.sequence.getStart();
    } else if (this.position == this.sequence.getEnd()) {
      this.index = this.freezePoints.size() - 2;
      return this.sequence.getEnd();
    }
    if (Math.abs(difference) <= FAST_FORWARD)
      return difference > 0 ? getNextFreezePoint(position) : getLastFreezePoint(
          position);
    return searchFreezePoint(position);
  }

  /**
   * Returns the frame affected by freezing based on the given position. This
   * method traverses over the freezing intervals forward from the current
   * index.
   */
  private int getNextFreezePoint(int position) {
    while (position >= this.freezePoints.get(this.index + 1)) {
      if (this.index + 2 >= this.freezePoints.size())
        return position;
      this.index += 2;
    }
    if (position < this.freezePoints.get(this.index))
      return position;
    return this.freezePoints.get(this.index);
  }

  /**
   * Returns the frame affected by freezing based on the given position. This
   * method traverses over the freezing intervals backward from the current
   * index.
   */
  private int getLastFreezePoint(int position) {
    while (position < this.freezePoints.get(this.index)) {
      if (this.index - 2 < 0)
        return position;
      this.index -= 2;
    }
    if (position >= this.freezePoints.get(this.index + 1))
      return position;
    return this.freezePoints.get(this.index);
  }

  /**
   * Returns the frame affected by freezing based on the given position. This
   * method traverses over the freezing intervals forward from the start.
   */
  private int searchFreezePoint(int position) {
    for (int i = 0; i < this.freezePoints.size(); i += 2)
      if (position >= this.freezePoints.get(i) && position < this.freezePoints
          .get(i + 1)) {
        this.index = i;
        return this.freezePoints.get(i);
      } else if (position < this.freezePoints.get(i)) {
        this.index = i;
        return position;
      }
    this.index = this.freezePoints.size() - 2;
    return position;
  }

  /**
   * Returns an immutable List of freezing intervals from the metadata file in
   * the directory pointed by the path of this FreezingSequenceWorker
   */
  private List<Integer> makeFreezePoints() throws
      IOException,
      MalformedSequenceException {
    List<Integer> out = new ArrayList<>();
    List<String> freezePoints;

    try {
      freezePoints = Arrays.asList(new ConfigFileReader(
          this.path + METADATA_FILE).read("freezePoints").split(",")
      );
    } catch (NoSuchElementException e) {
      throw new BadMetadataException("freezePoints");
    }
    if (freezePoints.get(freezePoints.size() - 1).equals(""))
      freezePoints.remove(freezePoints.size() - 1);
    for (int i = 0; i < freezePoints.size(); i++) {
      int freezePoint;

      try {
        freezePoint = Integer.parseInt(freezePoints.get(i));
      } catch (NumberFormatException e) {
        throw new BadMetadataException(freezePoints.get(i));
      }
      if ((i > 0 && freezePoint < out.get(i - 1)) || freezePoint < this.sequence
          .getStart() || freezePoint > this.sequence.getEnd())
        throw new BadFreezePointException(freezePoint);
      out.add(freezePoint);
    }
    if (out.size() % 2 != 0)
      out.add(this.sequence.getEnd());
    return Collections.unmodifiableList(out);
  }
}
