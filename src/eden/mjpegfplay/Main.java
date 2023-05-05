package eden.mjpegfplay;

import static eden.common.shared.Constants.EOL;
import static eden.common.shared.Constants.SPACE;
import static eden.mjpegfplay.model.ApplicationInformation.*;

import eden.mjpegfplay.presenter.ApplicationInstance;
import eden.mjpegfplay.view.ConsoleInterface;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This class serves as the entry point to this application. It contains the
 * main method from which the application initializes.
 *
 * @author Brendon
 * @version u0r5, 05/05/2023.
 */
public class Main {

  /**
   * The main method is the entry point to this application
   *
   * @param args Command-line arguments to be passed on execution
   */
  public static void main(String[] args) {
    boolean console = false;
    boolean noOpenGl = false;
    for (String s : args) {
      switch (s.toLowerCase()) {
        case "--console":
          console = true;
          break;
        case "--nativelaf":
          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          } catch (
            ClassNotFoundException
            | IllegalAccessException
            | InstantiationException
            | UnsupportedLookAndFeelException e
          ) {}
          break;
        case "--noopengl":
          noOpenGl = true;
      }
    }
    System.out.println(
      APPLICATION_NAME +
      SPACE +
      APPLICATION_VERSION +
      " by Brendon, " +
      APPLICATION_DATE +
      "." +
      EOL +
      "——" +
      APPLICATION_DESCRIPTION +
      SPACE +
      APPLICATION_URL +
      EOL +
      EOL +
      "Usage: --console --nativelaf --noopengl" +
      EOL +
      EOL +
      "The graphical interface will always be launched." +
      EOL
    );
    if (!noOpenGl) {
      System.setProperty("sun.java2d.opengl", "True");
    }
    if (console) {
      new ConsoleInterface().run();
    } else {
      new ApplicationInstance();
    }
  }
}
