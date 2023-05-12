package eden.mjpegfplay.view;

import static eden.common.shared.Constants.EOL;

import eden.mjpegfplay.presenter.exception.MalformedSequenceException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import javax.swing.JOptionPane;

/**
 * An ApplicationMessenger communicates between the end user and this
 * application. It takes foreground event notifications, input prompts, and
 * Exceptions.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
class ApplicationMessenger {

  /** Parent application UI */
  private final ApplicationUI ui;

  /** Makes an ApplicationMessenger with the given ApplicationUI */
  ApplicationMessenger(ApplicationUI ui) {
    this.ui = ui;
  }

  void sayInfo(String title, String message) {
    this.ui.frontPanel.setGui(true);
    this.ui.frontPanel.call();
    JOptionPane.showMessageDialog(
      this.ui.frameMain,
      message,
      title,
      JOptionPane.INFORMATION_MESSAGE
    );
    if (!this.ui.modal) {
      this.ui.frontPanel.setGui(false);
      this.ui.frontPanel.call();
    }
  }

  void sayWarning(String title, String message) {
    this.ui.frontPanel.setGui(true);
    this.ui.frontPanel.call();
    JOptionPane.showMessageDialog(
      this.ui.frameMain,
      message,
      title,
      JOptionPane.WARNING_MESSAGE
    );
    if (!this.ui.modal) {
      this.ui.frontPanel.setGui(false);
      this.ui.frontPanel.call();
    }
  }

  void sayError(String title, String message) {
    this.ui.frontPanel.setGui(true);
    this.ui.frontPanel.call();
    JOptionPane.showMessageDialog(
      this.ui.frameMain,
      message,
      title,
      JOptionPane.ERROR_MESSAGE
    );
    if (!this.ui.modal) {
      this.ui.frontPanel.setGui(false);
      this.ui.frontPanel.call();
    }
  }

  void sayMalformedSequenceException(MalformedSequenceException exception) {
    sayError(
      "Malformed Sequence Error",
      "Subject:" +
      EOL +
      exception.getSubject() +
      EOL +
      EOL +
      "Problem:" +
      EOL +
      exception.getProblem() +
      EOL +
      EOL +
      "Remedy: " +
      EOL +
      exception.getRemedy()
    );
  }

  int askInteger(String title, String message) {
    try {
      String in = askString(title, message);
      if (in == null) {
        return Integer.MIN_VALUE;
      }
      return Integer.parseInt(in);
    } catch (NumberFormatException exception) {
      sayError("Input Error", "Invalid integer.");
    }
    return Integer.MIN_VALUE;
  }

  String askString(String title, String message) {
    this.ui.frontPanel.setGui(true);
    this.ui.frontPanel.call();
    String out = JOptionPane.showInputDialog(
      this.ui.frameMain,
      message,
      title,
      JOptionPane.QUESTION_MESSAGE
    );
    if (!this.ui.modal) {
      this.ui.frontPanel.setGui(false);
      this.ui.frontPanel.call();
    }
    return out;
  }

  void sayException(Exception exception) {
    if (exception == null) {
      return;
    }
    String title, message = null;
    if (exception instanceof NumberFormatException) {
      title = "Number Format Error";
      message = "A numerical parameter is incorrectly formatted.";
    } else if (exception instanceof IOException) {
      title = "I/O Error";
    } else {
      title = "Error";
    }
    if (message != null) {
      sayError(title, message);
      return;
    }
    if (exception instanceof NoSuchFileException) {
      message =
        "The metadata file can not be found or opened." +
        EOL +
        exception.getMessage();
    } else {
      message =
        "An unrecognized error was thrown:" + EOL + exception.toString();
    }
    sayError(title, message);
  }
}
