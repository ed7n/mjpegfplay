package eden.common.clock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An {@code EDENTimer} provides accurate fixed-interval timing either on
 * itself, or upon an existing Java timer. It overcomes timing discrepancies
 * introduced by the low-precision nature of timers by adjusting its delay as
 * necessary, alternating between floor and ceil intervals.
 *
 * Java's timers run at the millisecond-level of precision. Non-integral delays
 * may introduce timing discrepancies. For example, for a fire rate of 60,
 * {@code (1000 / 60 = 16.67)} which is then truncated to an integral value. A
 * timer whose delay is set to this value may run either too fast (by 40 ms) or
 * too slow (by 20 ms), depending on the environment JVM runs on.
 *
 * A workaround is to add a housekeeping routine in atop the timer's tasks. This
 * routine calculates the difference in time between the current and last tick,
 * then adjusts the timer's delay to either floor, if the difference is too
 * large, or ceil, if the difference is too small.
 *
 * Subclasses are encouraged to utilize the counter for flexibility. Usually, it
 * is to be incremented on every tick within the range {@code [0, fireRate -
 * 1]}:
 *
 * {@code this.counter.set(((this.counter.get() + 1) % this.fireRate));}
 *
 * @author Brendon
 * @version u0r0, 11/25/2018.
 */
public abstract class EDENTimer {

  /** Default number of fires per second */
  public static final short DEFAULT_FIRE_RATE = 60;
  /** Internal counter for external tracking purposes, if needed. */
  protected AtomicInteger counter;
  /** Unix epoch when the Timer is last fired */
  protected long time;
  /** Number of event fires per second */
  protected short fireRate;
  /** Timer delay for floor mode */
  protected short delayActual;
  /** Timer delay for ceil mode */
  protected short delayOffset;
  /**
   * Indicates whether this EDENTimer is under ceil mode, although its main
   * purpose is to save computation time.
   */
  protected boolean clock;

  /**
   * Makes a {@code EDENTimer} with the default fire rate of {@value
   * #DEFAULT_FIRE_RATE}
   */
  public EDENTimer() {
    this(DEFAULT_FIRE_RATE);
  }

  /** Makes an {@code EDENTimer} with the given fire rate */
  public EDENTimer(short fireRate) {
    this.counter = new AtomicInteger(0);
    this.time = 0;
    this.fireRate =
      (fireRate < 1) || (fireRate > 1000) ? DEFAULT_FIRE_RATE : fireRate;
    this.clock = false;
    makeTimes();
  }

  /** Starts this {@code EDENTimer} */
  public abstract void start();

  /** Stops this {@code EDENTimer} */
  public abstract void pause();

  /** Stops and resets this {@code EDENTimer} */
  public abstract void stop();

  /** Fires this {@code EDENTimer} once */
  public abstract void tick();

  /** Returns the number of fires per second for this {@code EDENTimer} */
  public short getFireRate() {
    return this.fireRate;
  }

  /** Returns the internal counter of this {@code EDENTimer} */
  public int getCounter() {
    return this.counter.get();
  }

  /** Sets the number of fires per second for this {@code EDENTimer} */
  public abstract void setFireRate(short fireRate);

  /** Returns whether this {@code EDENTimer} is running */
  public abstract boolean isRunning();

  /** Calculates the numbers necessary to achieve an accurate timing */
  protected void makeTimes() {
    double quotient = (double) 1000 / this.fireRate;
    this.delayActual = (short) Math.floor(quotient);
    this.delayOffset = (short) Math.ceil(quotient);
  }
}
