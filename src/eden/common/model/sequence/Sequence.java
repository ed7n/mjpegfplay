package eden.common.model.sequence;

import java.util.Objects;

/**
 * A {@code Sequence} represents anything identifiable that has a finite
 * sequential nature.
 * <p>
 * "Things go from A to B to C, calling it a story changes nothing. It's just a
 * sequence. That's all it is."
 * <p>
 * Points of a {@code Sequence} are represented as {@code ints}.
 *
 * @author Brendon
 * @version u0r1, 11/28/2018.
 */
public class Sequence {

  /** Sequence name */
  protected final String name;

  /** Starting point */
  protected final int start;

  /** Ending point */
  protected final int end;

  /** Number of steps to advance per second */
  protected final int rate;

  /** Current point */
  protected int point;

  /** Number of points to advance per step */
  protected int skip;

  /** Makes a {@code Sequence} with the given name, start, and end points. */
  public Sequence(String name, int start, int end) {
    this(name, start, end, 0, 1, start);
  }

  /** Makes a {@code Sequence} with the given parameters */
  public Sequence(String name, int start, int end, int rate) {
    this(name, start, end, rate, 1, start);
  }

  /**
   * Makes a {@code Sequence} with the given parameters
   *
   * @param name Sequence name
   *
   * @param start Starting point
   *
   * @param end Ending point
   *
   * @param rate Number of steps to advance per second
   *
   * @param skip Number of points to advance per step
   *
   * @param point Current point
   */
  public Sequence(String name,
      int start,
      int end,
      int rate,
      int skip,
      int point) {
    this.name = name;
    this.start = start;
    this.end = end;
    this.rate = rate;
    this.point = point;
    this.skip = skip;
  }

  /** Makes a copy of a given {@code Sequence} */
  public Sequence(Sequence copy) {
    this.name = copy.name;
    this.start = copy.start;
    this.end = copy.end;
    this.rate = copy.rate;
    this.point = copy.start;
    this.skip = 0;
  }

  /**
   * Advances this {@code Sequence}
   *
   * @return {@code false} If this {@code Sequence} is already at its bounds;
   *
   * {@code true} If the operation is successful
   */
  public boolean advance() {
    if (!isValidPoint(this.point + this.skip))
      return false;
    this.point += this.skip;
    return true;
  }

  /** Advances to the start of this {@code Sequence} */
  public void goToStart() {
    this.point = this.start;
  }

  /** Advances to the end of this {@code Sequence} */
  public void goToEnd() {
    this.point = this.end;
  }

  /** Returns the name of this {@code Sequence} */
  public String getName() {
    return this.name;
  }

  /** Returns the starting point of this {@code Sequence} */
  public int getStart() {
    return this.start;
  }

  /** Returns the ending point of this {@code Sequence} */
  public int getEnd() {
    return this.end;
  }

  /** Returns the current point of this {@code Sequence} */
  public int getPoint() {
    return this.point;
  }

  /**
   * Returns the number of steps to advance per second for this {@code Sequence}
   */
  public int getRate() {
    return this.rate;
  }

  /**
   * Returns the number of points to advance per step for this {@code Sequence}
   */
  public int getSkip() {
    return this.skip;
  }

  /** Returns the length of this {@code Sequence} in number of points */
  public int getLength() {
    return this.end - this.start + 1;
  }

  /** Returns the length of this {@code Sequence} in seconds */
  public double getLengthSecond() {
    return (double) (this.end - this.start) / this.rate;
  }

  /** Returns the number of elapsed points for this {@code Sequence} */
  public int getElapsed() {
    return this.point - this.start;
  }

  /** Returns the elapsed time for this {@code Sequence} in seconds */
  public double getElapsedSecond() {
    return (double) (this.point - this.start) / this.rate;
  }

  /** Returns the percentual progress for this {@code Sequence} */
  public double getElapsedPercent() {
    return (double) (this.point - this.start) / (this.end - this.start);
  }

  /**
   * Sets the current point of this {@code Sequence}
   *
   * @return {@code false} If an invalid point is passed;
   *
   * {@code true} If the operation is successful
   */
  public boolean setPoint(int point) {
    if (!isValidPoint(point))
      return false;
    this.point = point;
    return true;
  }

  /** Sets the number of points to advance for this {@code Sequence} */
  public void setSkip(int skip) {
    this.skip = skip;
  }

  /** Returns whether the given point is valid for this {@code Sequence} */
  public boolean isValidPoint(int point) {
    return point >= this.start && point <= this.end;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object o) {
    return o == this || (o != null && o.getClass() == getClass() && equals(
        (Sequence) o));
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(name, start, end);
  }

  /** Returns whether the given {@code Sequence} is equivalent to this */
  public boolean equals(Sequence s) {
    return s != null && s.name.equals(this.name) && s.start == this.start
        && s.end == this.end && s.rate == this.rate;
  }
}
