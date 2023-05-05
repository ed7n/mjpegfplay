package eden.mjpegfplay.view;

import static eden.mjpegfplay.view.UIConstants.*;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * A CommandMenuItem is a GUI element on which the user can tap to send its
 * command to this application.
 *
 * This class is a convenient JMenuItem factory for menu items.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
class CommandMenuItem extends JMenuItem {

  /**
   * Makes a CommandMenuItem with the given label, actionCommand, and
   * ActionListener.
   */
  CommandMenuItem(String label, String actionCommand, ActionListener listener) {
    super(label);
    addActionListener(listener);
    setActionCommand(actionCommand);
    setBackground(COLOR_MENU);
    setFont(FONT_MENU);
  }
}
