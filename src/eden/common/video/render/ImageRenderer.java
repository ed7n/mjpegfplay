// @formatter:off
package eden.common.video.render;

import eden.common.io.active.ReadAheadLens;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;


/**
 *  An {@code ImageRenderer} renders {@code Images} on the {@code
 *  RendererComponent} that calls its draw method.
 *
 *  @author     Brendon
 *  @version    u0r0, 11/25/2018.
 *
 *  @see        EDENRenderer
 */
public class ImageRenderer extends EDENRenderer {

//~~PUBLIC CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Default rendering aspect ratio */
    public static final double DEFAULT_RATIO = 1;


//~~OBJECT FIELDS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Working ReadAheadLens */
    private ReadAheadLens<Image> lens;

    /** Current Image to be painted */
    private Image image;

    /** Rendering aspect ratio */
    private double ratio;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Makes an {@code ImageRenderer} with a {@code null} name, the default
     *  rendering aspect ratio, and no {@code RendererComponents}.
     */
    public ImageRenderer() {
        this(null, null, DEFAULT_RATIO, new RendererComponent[0]);
    }

    /** Makes an {@code ImageRenderer} with the given {@code ReadAheadLens} */
    public ImageRenderer(ReadAheadLens<Image> lens) {
        this(null, lens, DEFAULT_RATIO, new RendererComponent[0]);
    }

    /**
     *  Makes an {@code ImageRenderer} with the given {@code ReadAheadLens} and
     *  rendering aspect ratio
     */
    public ImageRenderer(ReadAheadLens<Image> lens, double ratio) {
        this(null, lens, ratio, new RendererComponent[0]);
    }

    /**
     *  Makes an {@code ImageRenderer} with the given name, {@code
     *  ReadAheadLens}, and rendering aspect ratio.
     */
    public ImageRenderer(String name, ReadAheadLens<Image> lens, double ratio) {
        this(name, lens, ratio, new RendererComponent[0]);
    }

    /**
     *  Makes an {@code ImageRenderer} with the given parameters
     */
    public ImageRenderer(String name,
                         ReadAheadLens<Image> lens,
                         double ratio,
                         RendererComponent... components)
    {
        super(name, components);
        this.lens  = lens;
        this.image = lens.poll();
        this.ratio = Math.abs(ratio);
    }


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Updates the {@code Image} of this {@code ImageRenderer} to reflect its
     *  latest state
     */
    @Override
    public void update(RendererComponent component, ActionEvent event) {
        Image image = this.lens.poll();

        if (image != null) {
            this.image = image;
        }
    }

    /**
     *  Draws the {@code Image} of this {@code ImageRenderer} with the given
     *  {@code Graphics2D}
     */
    @Override
    public void draw(RendererComponent component, Graphics2D g) {
        if (this.image == null) {
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
        g.drawImage(this.image,
            (int) Math.round(((double)
                component.getWidth() / 2) - ((double) width / 2)
            ),
            (int) Math.round(((double)
                component.getHeight() / 2) - ((double) height / 2)
            ),
            width, height, null
        );
        if (this.drawStatistics) {
            g.setFont(FONT);

            drawStatistics(
                component, g, width, height, time - this.time
            );
        }
        this.time = time;
    }


//~~~~ACCESSORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Returns the working {@code ReadAheadLens} of this {@code ImageRenderer}
     */
    public ReadAheadLens<Image> getLens() {
        return this.lens;
    }

    /** Returns the rendering aspect ratio of this {@code ImageRenderer} */
    public double getRatio() {
        return this.ratio;
    }


//~~~~MUTATORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Assigns the given working {@code ReadAheadLens} to this {@code
     *  ImageRenderer} and returns the previously assigned value
     */
    public ReadAheadLens<Image> setLens(ReadAheadLens<Image> lens) {
        if (lens == null) {
            return null;
        }
        ReadAheadLens<Image> out = this.lens;
        this.lens = lens;
        return out;
    }

    /** Sets the rendering aspect ratio of this {@code ImageRenderer} */
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    /**
     *  Flips the rendering aspect ratio of this {@code ImageRenderer}. This
     *  may be called upon display orientation changes.
     */
    public void flipRatio() {
        this.ratio = 1 / this.ratio;
    }


//~~PRIVATE OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Draws rendering statistics with the given parameters */
    private void drawStatistics(RendererComponent component,
                                       Graphics2D g,
                                              int width,
                                              int height,
                                             long time)
    {
        g.drawString(
            "Space: " + component.getWidth() + "×" + component.getHeight() +
            "  Output: " + width + "×" + height +
            "  Source: " + this.image.getWidth(null) + "×" +
                           this.image.getHeight(null),
        1, 13);

        g.drawString("Delta: " + time + " ms", 1, 28);
    }
}
