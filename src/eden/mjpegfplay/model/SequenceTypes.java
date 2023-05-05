package eden.mjpegfplay.model;

/**
 * This class provides definitions to the types of {@code Sequence} recognized
 * and supported by this application.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
public class SequenceTypes {

  /** The original (JPEG) image {@code Sequence} */
  public static final String SEQUENCE = "Sequence";
  /** Freezing image {@code Sequence} */
  public static final String FREEZING_SEQUENCE = "Freezing Sequence";
  /** Experimental music playlist */
  public static final String MUSIC_SEQUENCE = "Music";

  /** To prevent instantiations of this class */
  private SequenceTypes() {}
}
