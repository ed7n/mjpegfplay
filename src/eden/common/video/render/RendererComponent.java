package eden.common.video.render;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.swing.JComponent;

/**
 * A {@code RendererComponent} is a container on which graphical element(s) are
 * to be rendered.
 *
 * @author Brendon
 * @version u0r0, 11/25/2018.
 */
public class RendererComponent extends JComponent implements ActionListener {

  /** Identification */
  protected final String name;

  /** Renderers to paint onto this RendererComponent */
  private final Set<EDENRenderer> renderers;

  private final Dimension dimension;

  /** Makes a {@code RendererComponent} */
  public RendererComponent() {
    this(null, new EDENRenderer[0]);
  }

  /**
   * Makes a {@code RendererComponent} with the given {@code EDENRenderers}
   */
  public RendererComponent(EDENRenderer... renderers) {
    this(null, renderers);
  }

  /**
   * Makes a {@code RendererComponent} with the given name and {@code
   * EDENRenderers}
   */
  public RendererComponent(String name, EDENRenderer... renderers) {
    this.name = name;
    Set<EDENRenderer> set = new HashSet<>(Arrays.asList(renderers));
    set.remove(null);
    this.renderers = Collections.synchronizedSet(set);
    this.dimension = new Dimension();
  }

  /**
   * Have the {@code EDENRenderers} paint onto this {@code RendererComponent}.
   * <p>
   * To offer as much flexibility as possible, this does not clear but paints
   * over what is currently painted. To clear previous paints, add a {@code
   * CleaningRenderer} to this {@code RendererComponent} at your preference.
   */
  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();

    for (EDENRenderer r : this.renderers)
      r.draw(this, g2);
  }

  @Override
  public Dimension getPreferredSize() {
    this.dimension.setSize(getParent().getWidth(), getParent().getHeight());
    return this.dimension;
  }

  /**
   * Updates all {@code EDENRenderers} in this {@code RendererComponent} and
   * repaints itself
   */
  public void actionPerformed(ActionEvent event) {
    for (EDENRenderer r : this.renderers)
      r.update(this, event);
    repaint();
  }

  /**
   * Returns a {@code Set} of all the {@code EDENRenderers} in this {@code
   * RendererComponent}. The actual type returned is a {@code HashSet}.
   */
  public Set<EDENRenderer> getRenderers() {
    return new HashSet<>(this.renderers);
  }

  /** Adds the given {@code EDENRenderer} to this {@code RendererComponent} */
  public void addRenderer(EDENRenderer renderer) {
    if (renderer == null)
      return;
    this.renderers.add(renderer);
  }

  /**
   * Removes the given {@code EDENRenderer} from this {@code RendererComponent}
   *
   * @return {@code true} If the given {@code EDENRenderer} was found and is
   * successfully removed;
   *
   * {@code false} If the given {@code EDENRenderer} was not found or is null
   */
  public boolean removeRenderer(EDENRenderer renderer) {
    return renderer != null && this.renderers.remove(renderer);
  }

  /**
   * Removes all {@code EDENRenderers} from this {@code RendererComponent}
   */
  public void clearRenderers() {
    this.renderers.clear();
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object o) {
    return o == this || (o != null && o.getClass() == getClass() && equals(
        (RendererComponent) o));
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  /**
   * Returns whether the given {@code RendererComponent} is equivalent to this
   */
  public boolean equals(RendererComponent c) {
    return c != null && c.name.equals(this.name);
  }
}
