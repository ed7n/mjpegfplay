package eden.common.io.active;

import eden.common.model.sequence.FileFrameSequence;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * A {@code FileImageLens} reads and decodes image files into its fixed-capacity
 * FIFO buffer.
 *
 * @author Brendon
 * @version u0r0, 11/25/2018.
 *
 * @see ReadAheadLens
 */
public class FileImageLens extends ReadAheadLens<Image> {

  /** Path to working directory */
  private final String path;

  /** Working Sequence */
  private final FileFrameSequence sequence;

  /**
   * Indicates whether this FileImageLens is signalled for a change in behavior
   */
  private AtomicBoolean call;

  /**
   * Makes a {@code FileImageLens} with the given path and {@code
   * FileFrameSequence}
   */
  public FileImageLens(String path, FileFrameSequence sequence) {
    this(path, sequence, DEFAULT_CAPACITY);
  }

  /**
   * Makes a {@code FileImageLens} with the given path, {@code
   * FileFrameSequence}, and buffer capacity in number of {@code Images}.
   */
  public FileImageLens(String path,
      FileFrameSequence sequence,
      short capacity) {
    super(capacity);
    this.path = path;
    this.sequence = sequence;
    this.call = new AtomicBoolean(false);
  }

  /** Runs this {@code FileImageLens} */
  public void run() {
    if (this.dead.get())
      return;
    try {
      while (!Thread.currentThread().isInterrupted()) {
        int skip = this.sequence.getSkip();
        int frame = this.sequence.getPoint();

        while (!Thread.currentThread().isInterrupted() && !this.dead.get()) {
          if (this.call.get()) {
            reset();
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
          if (this.call.get()) {
            reset();
            break;
          }
          try {
            add(ImageIO.read(new File(
                this.path + frame + "." + this.sequence.getExtension()
            )));
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

  /** Signals this {@code FileImageLens} for a change in behavior */
  public void call() {
    this.call.set(true);

    synchronized (this) {
      notifyAll();
    }
  }

  /** Awaits this {@code FileImageLens} for a change in behavior */
  public synchronized void await() {
    try {
      while (this.call.get() || this.buffer.isEmpty())
        wait();
    } catch (InterruptedException e) {
    }
  }

  /** Resets the buffering operation */
  private void reset() {
    this.call.set(false);
    clear();
  }
}
