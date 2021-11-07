package eden.mjpegfplay.presenter;

/**
 * A {@code NullPresenter} is absolutely nothing but a safe placeholder for
 * {@code null Presenters}.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
public class NullPresenter implements Presenter {

  /** Does absolutely nothing */
  @Override
  public void call(int event) {
  }

  /** Does absolutely nothing */
  @Override
  public void call(String message0, String message1) {
  }
}
