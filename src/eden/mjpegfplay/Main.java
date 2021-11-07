package eden.mjpegfplay;

import eden.mjpegfplay.presenter.ApplicationInstance;

import eden.mjpegfplay.view.ConsoleInterface;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import static eden.mjpegfplay.model.ApplicationInformation.*;

/**
 * This class serves as the entry point to this application. It contains the
 * main method from which the application initializes.
 *
 * @author Brendon
 * @version u0r4, 11/06/2021.
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

    for (String s : args)
      switch (s.toLowerCase()) {
        case "--nativelaf":
          try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException
            | InstantiationException | UnsupportedLookAndFeelException e) {
        }
        break;
        case "--console":
          console = true;
          break;
        case "--noopengl":
          noOpenGl = true;
      }
    System.out.println(APPLICATION_NAME + " " + APPLICATION_VERSION + " "
        + "by Brendon," + " " + APPLICATION_DATE + ".\n" + "——"
        + APPLICATION_DESCRIPTION + " " + APPLICATION_URL + "\n");
    System.out.println("Usage: (--nativelaf|--console|--noopengl)...\n");
    if (!noOpenGl)
      System.setProperty("sun.java2d.opengl", "True");
    if (console)
      new ConsoleInterface().run();
    else
      new ApplicationInstance();
  }
}
