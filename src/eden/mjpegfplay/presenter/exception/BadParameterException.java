// @formatter:off
package eden.mjpegfplay.presenter.exception;


/**
 *  A {@code BadParameterException} is thrown when a sequence parameter is
 *  illegal.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
public class BadParameterException extends MalformedSequenceException {

    /** Makes a {@code BadParameterException} */
    public BadParameterException() {
        super(
"Sequence Parameters",
"The Sequence is defined to end before its starting point, or that its\n" +
    "rate, width, or height is at most 0.",
"Check the metadata file and correct any other offending parameters."
        );
    }
}
