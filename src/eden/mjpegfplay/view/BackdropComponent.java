// @formatter:off
package eden.mjpegfplay.view;

import eden.common.video.CSSColor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JComponent;


/**
 *  A {@code BackdropComponent} holds and draws the application backdrop image.
 *  <p>
 *  This class follows the singleton design pattern, because all {@code
 *  BackdropComponents} are equal anyways.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
class BackdropComponent extends JComponent {

//~~CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Singleton instance */
    static final BackdropComponent INSTANCE = new BackdropComponent();


//~~PRIVATE CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Font to draw render information with */
    private static final Font FONT;

    /** Font background Color */
    private static final Color COLOR = new Color(15, 3, 0);

    static {
        Map<TextAttribute, Object> map = new HashMap<>();
        map.put(TextAttribute.BACKGROUND, COLOR);
        map.put(TextAttribute.FAMILY, Font.MONOSPACED);
        map.put(TextAttribute.FOREGROUND, CSSColor.DARK_ORANGE);
        map.put(TextAttribute.SIZE, 12);
        FONT = new Font(map);
    }


//~~OBJECT CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Backdrop Image */
    private final Image image;

    /** Rendering Dimension in pixels */
    private final Dimension dimension;

    /** Rendering aspect ratio */
    private final double ratio;

    /** Indicates whether render statistics are to be drawn */
    private boolean drawStatistics;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Makes a BackdropComponent */
    private BackdropComponent() {
        Image image;

        try {
            image = ImageIO.read(getClass().getResource("/res/BACKDROP.JPG"));
        } catch (IOException e) {
            throw new RuntimeException(
                "The application backdrop image file can not be read: " +
                e.toString(), e
            );
        }
        this.image          = image;

        this.dimension      = new Dimension(
            image.getWidth(null), image.getHeight(null)
        );
        this.ratio      = (double) image.getWidth(null) / image.getHeight(null);
        this.drawStatistics = false;
    }


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Draws the backdrop of this {@code BackdropComponent} with the given
     *  {@code Graphics2D}
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int width;
        int height;

        if (getParent().getHeight() * this.ratio >= getParent().getWidth()) {
            width = getParent().getWidth();
            height = (int) Math.round(width / this.ratio);
        } else {
            height = getParent().getHeight();
            width = (int) Math.round(height * this.ratio);
        }
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        g2.drawImage(this.image,
            (int) Math.round(((double)
                getParent().getWidth() / 2) - ((double) width / 2)),
            (int) Math.round(((double)
                getParent().getHeight() / 2) - ((double) height / 2)
            ),
            width, height, null
        );
        if (this.drawStatistics) {
            g2.setFont(FONT);

            g2.drawString(
                "Space: " + getParent().getWidth()  + "×" +
                            getParent().getHeight() +
                "  Output: " + width + "×" + height +
                "  Source: " + this.image.getWidth( null) + "×" +
                               this.image.getHeight(null),
            1, 13);
        }
    }


//~~~~ACCESSORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Returns the {@code Dimension} of the parent {@code Component} */
    @Override
    public Dimension getPreferredSize() {
        this.dimension.setSize(getParent().getWidth(), getParent().getHeight());
        return this.dimension;
    }


//~~~~MUTATORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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
        repaint();
        return this.drawStatistics;
    }


//~~~~PREDICATES~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Returns whether render statistics are to be drawn */
    public boolean isDrawStatistics() {
        return this.drawStatistics;
    }
}
