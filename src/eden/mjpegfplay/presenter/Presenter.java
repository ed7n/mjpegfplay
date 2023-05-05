package eden.mjpegfplay.presenter;

/**
 * A {@code Presenter} provides the necessary interface to communicate to and/or
 * between {@code Presenters}
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
public interface Presenter {
  /** Notifies this {@code Presenter} of the given event */
  void call(int event);
  /** Notifies this {@code Presenter} of the given messages */
  void call(String message0, String message1);
}
