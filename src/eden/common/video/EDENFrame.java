// @formatter:off
package eden.common.video;

import java.awt.Image;
import java.util.Objects;


/**
 *  A {@code Frame} wraps an {@code Image} with an identifier.
 *
 *  @author     Brendon
 *  @version    u0r0, 11/25/2018.
 */
public class EDENFrame {

//~~OBJECT CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Image object */
    private final Image image;

    /** Image identifier */
    private final int identifier;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Makes a {@code Frame} with the given {@code Image} and identifier
     */
    public EDENFrame(Image image, int identifier) {
        this.image = image;
        this.identifier = identifier;
    }

    /** To prevent uninitialized instantiations of this class */
    private EDENFrame() {
        this.image = null;
        this.identifier = 0;
    }


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//~~~~ACCESSORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Returns the {@code Image} of this {@code Frame} */
    public Image getImage() {
        return this.image;
    }

    /** Returns the identifier of this {@code Frame} */
    public int getIdentifier() {
        return this.identifier;
    }


//~~~~OPERATORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public boolean equals(Object o) {
        return
            o            == this       ||
           (o            != null       &&
            o.getClass() == getClass() &&
            equals((EDENFrame) o));
    }

    /** @inheritDoc */
    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    /** Returns whether the given {@code Frame} is equivalent to this */
    public boolean equals(EDENFrame f) {
        return
            f != null                  &&
            f.image.equals(this.image) &&
            f.identifier == this.identifier;
    }
}
