// @formatter:off
package eden.mjpegfplay.view;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import static eden.mjpegfplay.model.ApplicationInformation.*;


/**
 *  An AboutDialog shows information about this application.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
class AboutDialog {

//~~OBJECT CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Message pane */
    private final JOptionPane pane = new JOptionPane(
APPLICATION_NAME + "\n" + APPLICATION_DESCRIPTION + "\n\n"                     +
APPLICATION_VERSION_LONG + " by Brendon, " + APPLICATION_DATE + ".      \n\n"  +
APPLICATION_URL + "\n\n"                                                       +
"This project was originally part of UBC CPSC210-2018W-T1. As such, I'd   \n"  +
"like to thank all instructors and TAs who took part of this course during\n"  +
"that time, especially the ones who gave me directions on designing this  \n"  +
"multi-threaded craziness . . .\n\n. . . it sure is very rewarding after"      +
" seeing that its end result is working.",
        JOptionPane.INFORMATION_MESSAGE
    );

    /** Dialog Container */
    private final JDialog dialog;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Makes an AboutDialog with the given parent JFrame */
    AboutDialog(JFrame parent) {
        this.dialog = this.pane.createDialog(parent, "About");
    }


//~~OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Shows this AboutDialog */
    void show() {
        this.dialog.setLocationRelativeTo(this.dialog.getParent());
        this.dialog.setVisible(true);
    }
}
