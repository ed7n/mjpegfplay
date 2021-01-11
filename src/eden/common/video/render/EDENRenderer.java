// @formatter:off
package eden.common.video.render;

import eden.common.video.CSSColor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 *  An {@code EDENRenderer} renders its graphical element(s) on the {@code
 *  RendererComponent} that calls its draw method. Optionally, it can keep a
 *  {@code List} of {@code RendererComponents} so that it knows to apply
 *  operations specific to each.
 *  <p>
 *  Two {@code EDENRenderer} with the same name are equal, regardless of the
 *  {@code RendererComponents} held by each.
 *
 *  @author     Brendon
 *  @version    u0r0, 11/25/2018.
 */
public abstract class EDENRenderer {

//~~PROTECTED CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Font with which to draw render statistics */
    protected static final Font FONT;

    /** Font background Color */
    protected static final Color COLOR = new Color(15, 3, 0);

    static {
        Map<TextAttribute, Object> map = new HashMap<>();
        map.put(TextAttribute.BACKGROUND, COLOR);
        map.put(TextAttribute.FAMILY    , Font.MONOSPACED);
        map.put(TextAttribute.FOREGROUND, CSSColor.DARK_ORANGE);
        map.put(TextAttribute.SIZE      , 12);
        FONT = new Font(map);
    }


//~~OBJECT CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Identification */
    protected final String name;

    /** RendererComponents to which this EDENRenderer paints */
    protected final List<RendererComponent> components;

    /** Unix epoch when the last draw occurred */
    protected long time;

    /** Indicates whether render statistics are to be drawn */
    protected boolean drawStatistics;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Makes an {@code EDENRenderer} with a {@code null} name and no {@code
     *  RendererComponents}
     */
    public EDENRenderer() {
        this(null, new RendererComponent[0]);
    }

    /**
     *  Makes an {@code EDENRenderer} with the given {@code RendererComponents}
     */
    public EDENRenderer(RendererComponent... components) {
        this(null, components);
    }

    /**
     *  Makes an {@code EDENRenderer} with the given name and {@code
     *  RendererComponents}
     */
    public EDENRenderer(String name, RendererComponent... components) {
        this.name = name;
        List<RendererComponent> list = Arrays.asList(components);
        list.removeAll(Collections.singletonList(null));
        this.components = Collections.synchronizedList(new LinkedList<>(list));
        this.time = System.currentTimeMillis();
        this.drawStatistics = false;
    }


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Updates the graphical element(s) of this {@code EDENRenderer} to reflect
     *  its latest state
     */
    public abstract void update(RendererComponent component, ActionEvent event);

    /**
     *  Draws the graphical element(s) of this {@code EDENRenderer} with the
     *  given {@code Graphics2D}
     */
    public abstract void draw(RendererComponent component, Graphics2D g);


//~~~~ACCESSORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Returns a {@code List} of all the {@code RendererComponents} in this
     *  {@code EDENRenderer}. The actual type to be returned is a {@code
     *  LinkedList}.
     */
    public List<RendererComponent> getComponents() {
        return new LinkedList<>(this.components);
    }


//~~~~MUTATORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Adds the given {@code RendererComponent} to this {@code EDENRenderer} */
    public void addComponent(RendererComponent component) {
        if (component == null) {
            return;
        }
        this.components.add(component);
    }

    /**
     *  Removes the given {@code RendererComponent} from this {@code
     *  EDENRenderer}
     *
     *  @return     {@code true}
     *              If the given {@code RendererComponent} was found and is
     *              successfully removed;
     *
     *              {@code false}
     *              If the given {@code RendererComponent} was not found or is
     *              {@code null}
     */
    public boolean removeComponent(RendererComponent component) {
        return component != null && this.components.remove(component);
    }

    /**
     *  Removes all {@code RendererComponents} from this {@code EDENRenderer}
     */
    public void clearComponents() {
        this.components.clear();
    }

    /** Sets whether render statistics are to be drawn */
    public void setDrawStatistics(boolean drawStatistics) {
        this.drawStatistics = drawStatistics;
    }

    /**
     *  Toggles whether render statistics are to be drawn and returns its new
     *  value
     */
    public boolean toggleDrawStatistics() {
        this.drawStatistics = !this.drawStatistics;
        return this.drawStatistics;
    }


//~~~~PREDICATES~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Returns whether render statistics are to be drawn */
    public boolean isDrawStatistics() {
        return this.drawStatistics;
    }


//~~~~OPERATORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public boolean equals(Object o) {
        return
            o            == this       ||
           (o            != null       &&
            o.getClass() == getClass() &&
            equals((EDENRenderer) o));
    }

    /** @inheritDoc */
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    /** Returns whether the given {@code EDENRenderer} is equivalent to this */
    public boolean equals(EDENRenderer r) {
        return
            r != null &&
            r.name.equals(this.name);
    }
}
