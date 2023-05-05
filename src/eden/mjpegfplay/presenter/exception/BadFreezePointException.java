package eden.mjpegfplay.presenter.exception;

import static eden.common.shared.Constants.EOL;

/**
 * A {@code BadFreezePointException} indicates that a freezing point is either
 * not greater or equal to the previous point, or is outside the range of points
 * for its {@code Sequence}. Check and correct any offending freeze points that
 * meet any of the aforementioned criteria.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
public class BadFreezePointException extends MalformedSequenceException {

  /**
   * Makes a {@code BadFreezePointException} with the given freeze point as its
   * detail message
   */
  public BadFreezePointException(int freezePoint) {
    super(
      "Freezing point: " + freezePoint,
      "This freezing point is either smaller than the previous point, or that it is" +
      EOL +
      "outside the bounds of its sequence.",
      "Check the sequence metadata file and correct this and any other offending" +
      EOL +
      "freezing points."
    );
  }
}
