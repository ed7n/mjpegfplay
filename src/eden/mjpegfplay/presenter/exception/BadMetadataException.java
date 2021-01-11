// @formatter:off
package eden.mjpegfplay.presenter.exception;


/**
 *  A {@code BadMetadataException} is thrown when a metadata file is incomplete
 *  or contains values that are formatted incorrectly.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
public class BadMetadataException extends MalformedSequenceException {

    /** Makes a {@code BadMetadataException} */
    public BadMetadataException() {
        super(
"Sequence Metadata File",
"It is incomplete or contains values that are incorrectly formatted.",
"Complete, review, or replace the metadata file, depending on the situation."
        );
    }

    /** Makes a {@code BadMetadataException} with the given subject */
    public BadMetadataException(String subject) {
        super(
"Sequence Metadata File - " + subject,
"It is incomplete or contains values that are incorrectly formatted.",
"Complete, review, or replace the metadata file, depending on the situation."
        );
    }
}
