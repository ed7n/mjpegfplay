package eden.common.clock;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * A {@code SyncroClock} provides accurate timing to a Java's Swing timer. This
 * overcomes timing discrepancies introduced by its low-precision nature by
 * adjusting its delay as necessary, alternating between floor and ceil
 * intervals.
 *
 * A {@code SyncroClock} is a (simplified) {@code Timer} with an additional
 * housekeeping {@code ActionListener}.
 *
 * @author Brendon
 * @version u0r0, 11/25/2018.
 *
 * @see EDENTimer
 * @see Timer
 */
public class SyncroClock extends EDENTimer {

  /** Working Timer */
  private final Timer timer;
  /** Tick ActionEvent */
  private final ActionEvent tickEvent;
  /** ActionListener that manages the alternating timing */
  private ActionListener listener;

  /**
   * Makes a {@code SyncroClock} with the default fire rate of {@value
   * #DEFAULT_FIRE_RATE}
   */
  public SyncroClock() {
    this(DEFAULT_FIRE_RATE);
  }

  /** Makes a {@code SyncroClock} with the given fire rate */
  public SyncroClock(short fireRate) {
    super(fireRate);
    this.timer = new Timer(0, null);
    this.tickEvent = new ActionEvent(this, ActionEvent.ACTION_LAST + 1, "tick");
    initialize();
  }

  /**
   * Starts the {@code Timer} of this {@code SyncroClock}. This causes it to
   * start sending {@code ActionEvents} to its listeners.
   */
  @Override
  public void start() {
    if (!this.timer.isRunning()) {
      this.timer.start();
    }
  }

  /**
   * Stops the {@code Timer} of this {@code SyncroClock}. This causes it to stop
   * sending {@code ActionEvents} to its listeners.
   */
  @Override
  public void pause() {
    this.timer.stop();
  }

  /**
   * Stops and resets this {@code SyncroClock}. This causes its {@code Timer} to
   * stop sending {@code ActionEvents} to its listeners.
   */
  @Override
  public void stop() {
    this.timer.stop();
    this.clock = false;
    this.counter.set(0);
  }

  /**
   * Restarts the {@code Timer} of this {@code SyncroClock}. This cancels any
   * pending firings and causing it to fire immediately.
   */
  public void restart() {
    this.timer.restart();
  }

  /**
   * Fires an {@code ActionEvent} to the {@code ActionListeners} registered on
   * the {@code Timer} of this {@code SyncroClock}
   */
  @Override
  public void tick() {
    for (ActionListener listener : this.timer.getActionListeners()) {
      listener.actionPerformed(this.tickEvent);
    }
  }

  /**
   * Returns an array of all the {@code ActionListeners} registered on the
   * {@code Timer} of this {@code SyncroClock}
   */
  public ActionListener[] getActionListeners() {
    return this.timer.getActionListeners();
  }

  /** Sets the number of fires per second for this {@code SyncroClock} */
  @Override
  public void setFireRate(short fireRate) {
    boolean wasRunning = this.timer.isRunning();
    stop();
    this.fireRate = fireRate;
    makeTimes();
    initialize();
    if (wasRunning) {
      this.timer.start();
    }
  }

  /**
   * Adds a given {@code ActionListener} to the {@code Timer} of this {@code
   * SyncroClock}
   */
  public void addActionListener(ActionListener listener) {
    this.timer.addActionListener(listener);
  }

  /**
   * Removes the given {@code ActionListener} from the {@code Timer} of this
   * {@code SyncroClock}
   */
  public void removeActionListener(ActionListener listener) {
    this.timer.removeActionListener(listener);
  }

  /**
   * Returns whether the {@code Timer} of this {@code SyncroClock} is running
   */
  @Override
  public boolean isRunning() {
    return this.timer.isRunning();
  }

  /**
   * Initializes the Timer, adding the necessary housekeeping ActionListener.
   */
  private void initialize() {
    if (this.listener != null) {
      this.timer.removeActionListener(this.listener);
    }
    this.listener =
      e -> {
        long time = System.currentTimeMillis();
        if ((!this.clock) && (time - this.time >= this.delayOffset)) {
          this.timer.setDelay(this.delayActual);
          this.clock = true;
        } else if ((this.clock) && (time - this.time <= this.delayActual)) {
          this.timer.setDelay(this.delayOffset);
          this.clock = false;
        }
        this.time = time;
        this.counter.set(((this.counter.get() + 1) % this.fireRate));
      };
    this.timer.addActionListener(this.listener);
  }
}
