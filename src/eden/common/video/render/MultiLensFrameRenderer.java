package eden.common.video.render;

import eden.common.io.active.FileFrameLens;
import eden.common.video.EDENFrame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

/**
 * A {@code MultiLensFrameRenderer} renders {@code Frames} on the {@code
 * RendererComponent} that calls its draw method.
 *
 * @author Brendon
 * @version u0r1, 11/06/2021.
 *
 * @see EDENRenderer
 */
public class MultiLensFrameRenderer extends EDENRenderer {

  /** Working FileFrameLenses */
  private final List<FileFrameLens> lenses;
  /** FileFrameLens from which the next Frame will be polled */
  private FileFrameLens lens;
  /** Current Frame to be painted */
  private EDENFrame frame;
  /** Rendering aspect ratio */
  private double ratio;
  /** Identifier of the last rendered Frame */
  private int lastIdentifier;
  private boolean reverse = false;

  /**
   * Makes an {@code MultiLensFrameRenderer} with the given {@code List} of
   * {@code FileFrameLens}
   */
  public MultiLensFrameRenderer(List<FileFrameLens> lenses) {
    this(null, lenses, 1, new RendererComponent[0]);
  }

  /**
   * Makes an {@code MultiLensFrameRenderer} with the given {@code List} of
   * {@code FileFrameLens} and rendering aspect ratio
   */
  public MultiLensFrameRenderer(List<FileFrameLens> lenses, double ratio) {
    this(null, lenses, ratio, new RendererComponent[0]);
  }

  /**
   * Makes an {@code MultiLensFrameRenderer} with the given parameters
   *
   * @param ratio Rendering aspect ratio
   */
  public MultiLensFrameRenderer(
    String name,
    List<FileFrameLens> lenses,
    double ratio
  ) {
    this(name, lenses, ratio, new RendererComponent[0]);
  }

  /**
   * Makes a {@code MultiLensFrameRenderer} with the given parameters
   *
   * @param ratio Rendering aspect ratio
   */
  public MultiLensFrameRenderer(
    String name,
    List<FileFrameLens> lenses,
    double ratio,
    RendererComponent... components
  ) {
    super(name, components);
    lenses.removeAll(Collections.singleton(null));
    this.lenses = Collections.unmodifiableList(lenses);
    this.lens = this.lenses.get(0);
    this.frame = this.lens.poll();
    this.ratio = Math.abs(ratio);
  }

  /** To prevent uninitialized instantiations of this class */
  private MultiLensFrameRenderer() {
    this.lenses = null;
  }

  /**
   * Updates the {@code Frame} of this {@code MultiLensFrameRenderer} to reflect
   * its latest state
   */
  @Override
  public void update(RendererComponent component, ActionEvent event) {
    if (!setNextLens()) {
      return;
    }
    EDENFrame frame = this.lens.poll();
    if (frame != null) {
      if (this.frame != null) {
        this.lastIdentifier = this.frame.getIdentifier();
      }
      synchronized (this) {
        this.frame = frame;
      }
    }
  }

  /**
   * Draws the {@code Frame} of this {@code MultiLensFrameRenderer} with the
   * given {@code Graphics2D}
   */
  @Override
  public void draw(RendererComponent component, Graphics2D g) {
    if (this.frame == null) {
      return;
    }
    long time = System.currentTimeMillis();
    int width;
    int height;
    if (component.getHeight() * this.ratio >= component.getWidth()) {
      width = component.getWidth();
      height = (int) Math.round(width / this.ratio);
    } else {
      height = component.getHeight();
      width = (int) Math.round(height * this.ratio);
    }
    g.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_SPEED
    );
    g.drawImage(
      this.frame.getImage(),
      (int) Math.round(
        ((double) component.getWidth() / 2) - ((double) width / 2)
      ),
      (int) Math.round(
        ((double) component.getHeight() / 2) - ((double) height / 2)
      ),
      width,
      height,
      null
    );
    if (this.drawStatistics) {
      g.setFont(FONT);
      drawStatistics(component, g, width, height, time - this.time);
    }
    this.time = time;
  }

  /**
   * Returns the identifier of the {@code Frame} of this {@code
   * MultiLensFrameRenderer}
   *
   * @return current identifier;
   *
   * {@code #Integer.MIN_VALUE} If the {@code Frame} of this
   * {@code MultiLensFrameRenderer} is {@code null}
   */
  public synchronized int getFrame() {
    return this.frame == null ? Integer.MIN_VALUE : this.frame.getIdentifier();
  }

  /**
   * Returns the rendering aspect ratio of this {@code MultiLensFrameRenderer}
   */
  public double getRatio() {
    return this.ratio;
  }

  /**
   * Sets the rendering aspect ratio of this {@code MultiLensFrameRenderer}
   */
  public void setRatio(double ratio) {
    this.ratio = ratio;
  }

  /**
   * Flips the rendering aspect ratio of this {@code MultiLensFrameRenderer}.
   * This may be called upon display orientation changes.
   */
  public void flipRatio() {
    this.ratio = 1 / this.ratio;
  }

  public void setReverse(boolean reverse) {
    this.reverse = reverse;
  }

  /**
   * Assigns the FileFrameLens from which the next Frame is to be polled on the
   * next update
   */
  private boolean setNextLens() {
    if (this.lenses.size() == 1) {
      return true;
    }
    int initialId = this.reverse
      ? Integer.MIN_VALUE
      : Integer.MAX_VALUE, nearestId = initialId;
    for (FileFrameLens l : this.lenses) {
      int id = l.getNextIdentifier();
      //    if ((this.frame == null
      //        || reverse && id < this.frame.getIdentifier()
      //        || (!reverse && id > this.frame.getIdentifier()))
      //        && (reverse && id > nearestId
      //        || (!reverse && id < nearestId))) {
      //      nearestId = id;
      //      this.lens = l;
      //    } else if (this.frame != null
      //        && (reverse && id >= this.frame.getIdentifier()
      //        || (!reverse && id <= this.frame.getIdentifier())))
      //      l.discard();
      if (reverse && id > nearestId || (!reverse && id < nearestId)) {
        nearestId = id;
        this.lens = l;
      }
    }
    return nearestId != initialId;
  }

  /** Draws rendering statistics with the given parameters */
  private void drawStatistics(
    RendererComponent component,
    Graphics2D g,
    int width,
    int height,
    long time
  ) {
    g.drawString(
      "Space: " +
      component.getWidth() +
      "×" +
      component.getHeight() +
      "  Output: " +
      width +
      "×" +
      height +
      "  Source: " +
      this.frame.getImage().getWidth(null) +
      "×" +
      this.frame.getImage().getHeight(null),
      1,
      13
    );
    g.drawString(
      "Delta: " +
      (this.frame.getIdentifier() - this.lastIdentifier) +
      "  Time: " +
      time +
      " ms",
      1,
      28
    );
    g.drawString(
      "Lens : " +
      this.lenses.indexOf(this.lens) +
      "/" +
      this.lenses.size() +
      "  Fill: " +
      this.lens.getUsed() +
      "/" +
      this.lens.getCapacity(),
      1,
      43
    );
    g.drawString("Frame: " + this.frame.getIdentifier(), 1, 58);
  }
}
