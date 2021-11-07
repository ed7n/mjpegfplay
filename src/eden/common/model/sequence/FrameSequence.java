package eden.common.model.sequence;

import java.util.Objects;

/**
 * A {@code FrameSequence} represents an image sequence.
 *
 * @author Brendon
 * @version u0r1, 11/28/2018.
 */
public class FrameSequence extends Sequence {

  /** Projection width in pixels */
  protected final short width;

  /** Projection height in pixels */
  protected final short height;

  /**
   * Makes a {@code FrameSequence} with the given parameters
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
   * @throws IllegalArgumentException If any of the numerical arguments are
   * malformed
   */
  public FrameSequence(String name,
      int start,
      int end,
      byte rate,
      short width,
      short height)
      throws IllegalArgumentException {
    super(name, start, end, rate, 0, start);
    validateInstantiation(start, end, rate, width, height);
    this.width = width;
    this.height = height;
  }

  /** Makes a copy of a given {@code FrameSequence} */
  public FrameSequence(FrameSequence copy) {
    super(copy);
    this.width = copy.width;
    this.height = copy.height;
  }

  /** Returns the projection width of this {@code FrameSequence} in pixels */
  public short getWidth() {
    return this.width;
  }

  /** Returns the projection height of this {@code FrameSequence} in pixels */
  public short getHeight() {
    return this.height;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object o) {
    return o == this || (o != null && o.getClass() == getClass() && equals(
        (FrameSequence) o));
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(
        name, start, end, rate, width, height
    );
  }

  /** Returns whether the given {@code FrameSequence} is equivalent to this */
  public boolean equals(FrameSequence s) {
    return s != null && s.width == this.width && s.height == this.height
        && super.equals(s);
  }

  /** Validates the given instantiation arguments */
  private void validateInstantiation(int start,
      int end,
      byte rate,
      short width,
      short height)
      throws IllegalArgumentException {
    if (end <= start || rate <= 0 || width <= 0 || height <= 0)
      throw new IllegalArgumentException();
  }
}
