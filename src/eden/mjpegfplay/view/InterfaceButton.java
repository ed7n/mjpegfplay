// @formatter:off
package eden.mjpegfplay.view;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import static eden.mjpegfplay.view.UIConstants.*;


/**
 *  A InterfaceButton is a GUI element on which the user can tap to control
 *  sequence playback.
 *  <p>
 *  This class is a convenient JButton factory for transport buttons.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
class InterfaceButton extends JButton {

//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Makes a InterfaceButton with the given label, actionCommand, and
     *  ActionListener.
     */
    InterfaceButton(String label, String actionCommand, ActionListener listener)
    {
        this(label, actionCommand, listener, null);
    }

    /** Makes a InterfaceButton with the given parameters */
    InterfaceButton(String label,
                    String actionCommand,
            ActionListener listener,
                    String tooltip)
    {
        super(label);
        addActionListener(listener);
        setActionCommand(actionCommand);
        setBorder(BUTTON_BORDER);
        setFont(FONT_BUTTON);
        setHorizontalTextPosition(SwingConstants.LEADING);
        setToolTipText(tooltip);
    }
}
