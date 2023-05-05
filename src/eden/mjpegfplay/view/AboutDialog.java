package eden.mjpegfplay.view;

import static eden.common.shared.Constants.EOL;
import static eden.common.shared.Constants.SPACE;
import static eden.mjpegfplay.model.ApplicationInformation.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * An AboutDialog shows information about this application.
 *
 * @author Brendon
 * @version u0r5, 05/05/2023.
 */
class AboutDialog {

  /** Message pane */
  private final JOptionPane pane = new JOptionPane(
    APPLICATION_NAME +
    SPACE +
    APPLICATION_VERSION +
    " by Brendon, " +
    APPLICATION_DATE +
    "." +
    EOL +
    "—" +
    APPLICATION_DESCRIPTION +
    SPACE +
    APPLICATION_URL +
    EOL +
    EOL +
    "This project was originally part of UBC CPSC210-2018W-T1. As such, I'd like to" +
    EOL +
    "thank all instructors and teaching assistants who took part of that course.",
    JOptionPane.INFORMATION_MESSAGE
  );
  /** Dialog Container */
  private final JDialog dialog;

  /** Makes an AboutDialog with the given parent JFrame */
  AboutDialog(JFrame parent) {
    this.dialog = this.pane.createDialog(parent, "About");
  }

  /** Shows this AboutDialog */
  void show() {
    this.dialog.setLocationRelativeTo(this.dialog.getParent());
    this.dialog.setVisible(true);
  }
}
