// @formatter:off
package eden.mjpegfplay;

import eden.mjpegfplay.presenter.ApplicationInstance;

import eden.mjpegfplay.view.ConsoleInterface;

import javax.swing.UIManager;

import static eden.mjpegfplay.model.ApplicationInformation.*;


/**
 *  This class serves as the entry point to this application. It contains the
 *  main method from which the application initializes.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
public class Main {

    /**
     *  The main method is the entry point to this application
     *
     *  @param      args
     *              Command-line arguments to be passed on execution
     */
    public static void main(String[] args) {
        boolean console = false;
        boolean noOpenGl = false;

        for (String s : args) {
            switch (s.toLowerCase()) {
                case "help":
                    return;
                case "nativelook":
                    try {
                        UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName()
                        );
                    } catch (Exception e){}
                    break;
                case "console":
                    console = true;
                    break;
                case "noopengl":
                    noOpenGl = true;
            }
        }
        if (!noOpenGl) {
            System.setProperty("sun.java2d.opengl", "True");
        }
        System.out.print("\n\n" +
            APPLICATION_NAME + "\n" +
            "----------\n" +
            APPLICATION_VERSION + " by Brendon, " + APPLICATION_DATE + ".\n\n"
        );
        if (console) {
            new ConsoleInterface().run();
            return;
        }
        new ApplicationInstance();
    }
}
