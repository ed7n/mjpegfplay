package eden.common.shared;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Shared independent constants.
 *
 * @author Brendon
 */
public final class Constants {

  public static final InputStream STDIN = System.in;
  public static final PrintStream STDOUT = System.out;
  public static final PrintStream STDERR = System.err;
  public static final String EOL = System.lineSeparator();
  public static final String NUL_STRING = "";
  public static final String SPACE = " ";
  public static final long NUL_LONG = Long.MIN_VALUE;
  public static final int BYTES_CAPACITY = 4096;
  public static final int EXIT_FAILURE = 1;
  public static final int EXIT_SUCCESS = 0;
  public static final int LINE_WIDTH = 80;
  public static final int NUL_INT = Integer.MIN_VALUE;
  public static final int NUL_STRING_HASH = NUL_STRING.hashCode();
  public static final int STRING_CAPACITY = 1024;
  public static final char NUL_CHAR = Character.MIN_VALUE;
  public static final byte NUL_BYTE = Byte.MIN_VALUE;

  /** To prevent instantiations of this class. */
  private Constants() {}
}
