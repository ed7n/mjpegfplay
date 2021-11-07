package eden.common.io.active;

import eden.common.model.sequence.FileFrameSequence;
import eden.common.video.EDENFrame;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * A {@code FileFrameLens} reads and decodes image files into its fixed-capacity
 * FIFO buffer with an identifier for each.
 *
 * @author Brendon
 * @version u0r1, 11/06/2021.
 *
 * @see ReadAheadLens
 */
public class FileFrameLens extends ReadAheadLens<EDENFrame> {

  /** Path to working directory */
  private final String path;

  /** Working FileFrameSequence */
  private final FileFrameSequence sequence;

  /** Frame number offset multiplier */
  private final byte offsetFrame;

  /** Frame skip multiplier */
  private final byte offsetSkip;

  /**
   * Indicates whether this FileFrameLens is signalled for a change in behavior
   */
  private AtomicBoolean call;

  /**
   * This is a buffering parameter that is to be obtained from the working
   * Sequence on call from outsider Threads, and then assigned onto the
   * buffering Thread's local copy on its next cycle.
   */
  private int frame;

  /**
   * This is a buffering parameter that is to be obtained from the working
   * Sequence on call from outsider Threads, and then assigned onto the
   * buffering Thread's local copy on its next cycle.
   */
  private int skip;

  /**
   * Makes a {@code FileFrameLens} with the given path and {@code
   * FileFrameSequence}
   */
  public FileFrameLens(String path, FileFrameSequence sequence) {
    this(path, sequence, DEFAULT_CAPACITY, (byte) 0, (byte) 0);
  }

  /**
   * Makes a {@code FileFrameLens} with the given path, {@code
   * FileFrameSequence}, and buffer capacity in number of {@code Frames}.
   */
  public FileFrameLens(String path,
      FileFrameSequence sequence,
      short capacity) {
    this(path, sequence, capacity, (byte) 0, (byte) 0);
  }

  /** Makes a {@code FileFrameLens} with the given parameters */
  public FileFrameLens(String path,
      FileFrameSequence sequence,
      byte offsetFrame,
      byte offsetSkip) {
    this(path, sequence, DEFAULT_CAPACITY, offsetFrame, offsetSkip);
  }

  /** Makes a {@code FileFrameLens} with the given parameters */
  public FileFrameLens(String path,
      FileFrameSequence sequence,
      short capacity,
      byte offsetFrame,
      byte offsetSkip) {
    super(capacity);
    this.path = path;
    this.sequence = sequence;
    this.offsetFrame = offsetFrame;
    this.offsetSkip = offsetSkip;
    this.call = new AtomicBoolean(false);
    updateBufferingParameters();
  }

  /** Runs this {@code FileFrameLens} */
  public void run() {
    if (this.dead.get())
      return;
    try {
      while (!Thread.currentThread().isInterrupted()) {
        int skip = this.skip;
        int frame = this.frame;

        while (!Thread.currentThread().isInterrupted() && !this.dead.get()) {
          if (this.call.compareAndSet(true, false)) {
            clear();
            break;
          }
          if ((this.buffer.size() >= this.capacity) || !this.sequence
              .isValidPoint(frame))
            try {
            synchronized (this) {
              wait();
            }
          } catch (InterruptedException e) {
            return;
          }
          if (this.call.compareAndSet(true, false)) {
            clear();
            break;
          }
          try {
            add(new EDENFrame(ImageIO.read(new File(
                this.path + frame + "." + this.sequence.getExtension()
            )), frame));
          } catch (IIOException e) {
            add(null);
          }
          frame += skip;
        }
      }
    } catch (IOException | NullPointerException e) {
      die(e);
    }
  }

  /** Signals this {@code FileFrameLens} for a change in behavior */
  public void call() {
    updateBufferingParameters();
    this.call.set(true);

    synchronized (this) {
      notifyAll();
    }
  }

  /** Awaits this {@code FileFrameLens} for a change in behavior */
  public synchronized void await() {
    try {
      while (this.call.get() || this.buffer.isEmpty())
        wait();
    } catch (InterruptedException e) {
    }
  }

  /**
   * Returns the vectorized identifier of the front most {@code Frame} in the
   * FIFO buffer of this {@code FileFrameLens}. If the buffer is empty or the
   * front most {@code Frame} is {@code null}, then the maximum {@code int} is
   * returned.
   */
  public int getNextIdentifier() {
    EDENFrame frame;

    synchronized (this) {
      frame = this.buffer.peek();
    }
    if (frame == null)
      return this.sequence.getSkip() >= 0
          ? Integer.MAX_VALUE : Integer.MIN_VALUE;
    else
      return frame.getIdentifier();
  }

  /**
   * Updates the buffering parameters from the working Sequence of this
   * FileFrameLens
   */
  private void updateBufferingParameters() {
    this.frame = this.sequence.getPoint() + (this.sequence.getSkip()
        * this.offsetFrame);

    this.skip = this.sequence.getSkip() * (1 + this.offsetSkip);
  }
}
