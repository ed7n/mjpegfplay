package eden.common.io.active;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@code ReadAheadLens} reads, then decodes, and/or process data chunks into
 * its fixed-capacity FIFO buffer at any pace. Any object with it as a field can
 * then retrieve the buffered data chunks at its pace. This reduces the
 * bottlenecks from direct I/O operations, especially from decoding and
 * processing.
 *
 * @author Brendon
 * @version u0r0, 11/25/2018.
 */
public abstract class ReadAheadLens<T> implements Runnable {

  /** Default buffer capacity in number of data chunks */
  public static final short DEFAULT_CAPACITY = 4;

  /** FIFO buffer to which data chunks are to be stored */
  protected final Queue<T> buffer;

  /** Buffer capacity in number of data chunks */
  protected final short capacity;

  /** Exception defining the death of this ReadAheadLens */
  protected Exception deathCause;

  /**
   * Indicates whether this ReadAheadLens was unrecoverably thrown an Exception
   */
  protected AtomicBoolean dead;

  /**
   * Makes an {@code ReadAheadLens} with the default buffer capacity in number
   * of data chunks
   */
  public ReadAheadLens() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Makes an {@code ReadAheadLens} with the given buffer capacity in number of
   * data chunks
   */
  public ReadAheadLens(short capacity) {
    this.buffer = new LinkedList<>();
    this.capacity = capacity > 0 ? capacity : DEFAULT_CAPACITY;
    this.deathCause = null;
    this.dead = new AtomicBoolean(false);
  }

  /** Runs this {@code ReadAheadLens} */
  public abstract void run();

// Skeleton.
//  public void run() {
//    if (this.dead.get())
//      return;
//    try {
//      while (!Thread.currentThread().isInterrupted() && !this.dead.get()) {
//        getNextDataChunk();
//        add(dataChunk);
//      }
//    } catch (Exception e) {
//      this.dead.set(true);
//    }
//  }
  /**
   * Removes and returns the head data chunk out of the FIFO buffer of this
   * {@code ReadAheadLens}
   */
  public synchronized T poll() {
    T out = this.buffer.poll();
    notifyAll();
    return out;
  }

  /**
   * Removes the head data chunk out of the FIFO buffer of this {@code
   * ReadAheadLens}
   */
  public synchronized void discard() {
    this.buffer.poll();
    notifyAll();
  }

  /**
   * Removes all data chunks from the FIFO buffer of this {@code ReadAheadLens}
   */
  public synchronized void clear() {
    this.buffer.clear();
  }

  /**
   * Returns the buffer capacity of this {@code ReadAheadLens} in number of data
   * chunks
   */
  public int getCapacity() {
    return this.capacity;
  }

  /**
   * Returns the amount of space used in the buffer of this {@code
   * ReadAheadLens} in number of data chunks
   */
  public int getUsed() {
    return this.buffer.size();
  }

  /**
   * Returns the amount of space free in the buffer of this {@code
   * ReadAheadLens} in number of data chunks
   */
  public int getFree() {
    return this.capacity - this.buffer.size();
  }

  /**
   * Returns the {@code Exception} defining the death of this {@code
   * ReadAheadLens}
   *
   * @return Cause of death;
   *
   * {@code null} If this {@code ReadAheadLens} is not dead
   */
  public Exception getDeathCause() {
    return this.deathCause;
  }

  /**
   * Returns whether this {@code ReadAheadLens} was thrown an {@code
   * IOException}
   */
  public boolean isDead() {
    return this.dead.get();
  }

  /** Adds the given data chunk to the FIFO buffer of this ReadAheadLens */
  protected synchronized void add(T data) {
    this.buffer.add(data);
    notifyAll();
  }

  /** Marks this ReadAheadLens dead with the given Exception as its cause */
  protected void die(Exception exception) {
    this.dead.set(true);
    this.deathCause = exception;
  }
}
