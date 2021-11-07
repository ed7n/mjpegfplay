package eden.mjpegfplay.model;

/**
 * This class provides definitions that describe this application.
 *
 * @author Brendon
 * @version u0r4, 11/06/2021.
 */
public class ApplicationInformation {

  /** Application name */
  public static final String APPLICATION_NAME = "mJPEGfPlay";

  /** Application version */
  public static final String APPLICATION_VERSION = "u0r4";

  /** Application version, long version. */
  public static final String APPLICATION_VERSION_LONG = "Update 0 Revision 4";

  /** Application release date */
  public static final String APPLICATION_DATE = "11/06/2021";

  /** Application description */
  public static final String APPLICATION_DESCRIPTION
      = "Currently an image file sequence player.";

  /** Application landing URL */
  public static final String APPLICATION_URL
      = "https://ed7n.github.io/mJPEGfPlay";

  /** To prevent instantiations of this class */
  private ApplicationInformation() {
  }
}
