package eden.common.clock;

/**
 * A {@code SimpleSyncroTimer} is a simple timer whose tasks are defined by a
 * {@code Runnable}, alongside the necessary housekeeping.
 *
 * @author Brendon
 * @version u0r0, 11/25/2018.
 */
public class SimpleSyncroTimer extends EDENTimer implements Runnable {

  /** Timer task */
  private Runnable runnable;
  /** Timer Thread */
  private Thread thread;
  /** Timer delay */
  private short delay;
  /** Indicates whether this SimpleSyncroTimer is running */
  private boolean running;

  /**
   * Makes a {@code SimpleSyncroTimer} with the given {@code Runnable} and the
   * default fire rate of {@value #DEFAULT_FIRE_RATE}
   */
  public SimpleSyncroTimer(Runnable runnable) {
    this(runnable, DEFAULT_FIRE_RATE);
  }

  /**
   * Makes a {@code SimpleSyncroTimer} with the given {@code Runnable} and fire
   * rate
   */
  public SimpleSyncroTimer(Runnable runnable, short fireRate) {
    super(fireRate);
    this.runnable = runnable;
    this.thread = new Thread(this);
    this.running = false;
    initialize();
  }

  /**
   * Runs this {@code SimpleSyncroTimer}. Call this method on a separate
   * {@code Thread}, although it should be noted that {@code SimpleSyncroTimers}
   * have their own {@code Thread} to run on.
   */
  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) try {
      if (!running) synchronized (this) {
        wait();
      }
      this.runnable.run();
      track();
      Thread.sleep(this.delay);
    } catch (InterruptedException e) {
      return;
    }
  }

  /** Starts this {@code SimpleSyncroTimer} */
  @Override
  public void start() {
    this.running = true;
    if (this.thread.isInterrupted()) {
      this.thread = new Thread(this);
      this.thread.start();
    } else synchronized (this) {
      notifyAll();
    }
  }

  /** Stops this {@code SimpleSyncroTimer} */
  @Override
  public void pause() {
    this.running = false;
  }

  /**
   * Stops this {@code SimpleSyncroTimer}. This is equivalent to calling the
   * {@code pause} method.
   */
  @Override
  public void stop() {
    this.running = false;
  }

  /**
   * Fires this {@code SimpleSyncroTimer} once. This simply calls the {@code
   * run} method of its {@code Runnable}.
   */
  @Override
  public void tick() {
    this.runnable.run();
  }

  /**
   * Interrupts the {@code Thread} on which this {@code SimpleSyncroTimer} runs
   */
  public void end() {
    this.thread.interrupt();
    try {
      this.thread.join();
    } catch (InterruptedException e) {}
  }

  /** Returns the {@code Runnable} of this {@code SimpleSyncroTimer} */
  public Runnable getRunnable() {
    return this.runnable;
  }

  /** Sets the number of fires per second for this {@code SimpleSyncroTimer} */
  @Override
  public void setFireRate(short fireRate) {
    if (fireRate < 1 || fireRate > 1000) {
      return;
    }
    this.fireRate = fireRate;
    makeTimes();
  }

  /** Sets the {@code Runnable} of this {@code SimpleSyncroTimer} */
  public void setRunnable(Runnable runnable) {
    this.runnable = runnable;
  }

  /** Returns this {@code SimpleSyncroTimer} is running */
  @Override
  public boolean isRunning() {
    return this.running;
  }

  /** Housekeeping routine */
  private void track() {
    long time = System.currentTimeMillis();
    if ((!this.clock) && (time - this.time >= this.delayOffset)) {
      this.delay = this.delayActual;
      this.clock = true;
    } else if ((this.clock) && (time - this.time <= this.delayActual)) {
      this.delay = this.delayOffset;
      this.clock = false;
    }
    this.time = time;
    this.counter.set(((this.counter.get() + 1) % this.fireRate));
  }

  /** Runs this SimpleSyncroTimer, a Runnable, on its Thread. */
  private void initialize() {
    this.thread.start();
  }
}
