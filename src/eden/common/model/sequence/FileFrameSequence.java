package eden.common.model.sequence;

import java.util.Objects;

/**
 * A {@code FileFrameSequence} represents an image sequence in which each frame
 * is a file.
 *
 * @author Brendon
 * @version u0r1, 11/28/2018.
 */
public class FileFrameSequence extends FrameSequence {

  /** Image file extension */
  private final String extension;

  /**
   * Makes a {@code FileFrameSequence} with the given parameters
   *
   * @param name Sequence name
   *
   * @param start Starting point
   *
   * @param end Ending point
   *
   * @param rate Number of steps to advance per second
   *
   * @param width Projection width in pixels
   *
   * @param height Projection height in pixels
   *
   * @param extension Image file extension
   *
   * @throws IllegalArgumentException If any of the numerical arguments are
   * malformed
   */
  public FileFrameSequence(String name,
      int start,
      int end,
      byte rate,
      short width,
      short height,
      String extension)
      throws IllegalArgumentException {
    super(name, start, end, rate, width, height);
    this.extension = extension;
  }

  /** Makes a copy of a given {@code FileFrameSequence} */
  public FileFrameSequence(FileFrameSequence copy) {
    super(copy);
    this.extension = copy.extension;
  }

  /** Returns the image file extension of this {@code FileFrameSequence} */
  public String getExtension() {
    return this.extension;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object o) {
    return o == this || (o != null && o.getClass() == getClass() && equals(
        (FileFrameSequence) o));
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(
        name, start, end, rate, width, height, extension
    );
  }

  /**
   * Returns whether the given {@code FileFrameSequence} is equivalent to this
   */
  public boolean equals(FileFrameSequence s) {
    return s != null && s.extension.equals(this.extension) && super.equals(s);
  }
}
