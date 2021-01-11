// @formatter:off
package eden.mjpegfplay.view;

import eden.mjpegfplay.presenter.ApplicationInstance;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static eden.mjpegfplay.model.ApplicationInformation.*;
import static eden.mjpegfplay.model.SequenceTypes.*;


/**
 *  This class represents the fallback view in the model-view-presenter
 *  architectural pattern of this application.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
public class ConsoleInterface implements Runnable {

//~~PUBLIC CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Default number of input attempts */
    public static final byte INPUT_ATTEMPTS = 3;


//~~PRIVATE CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final String[] MAIN = new String[]{
        "File", "View", "Transport", "Audio", "Help"
    };
    private static final String[] FILE = new String[]{
        "New", "Open", "Open Freezing", "Save", "Reload", "Close"
    };
    private static final String[] VIEW = new String[]{
        "Sequence Information"
    };
    private static final String[] TRANSPORT = new String[]{
        "{->} Play",         "{||} Pause",         "{[]} Stop",
        "{<<} Fast Rewind",  "{>>} Fast Forward",  "{<|} Step Backward",
        "{|>} Step Forward", "{|<} Jump To Start", "{>|} Jump To End",
        "{>#} Jump To...",   "{<-} Trickplay"
    };
    private static final String[] HELP = new String[]{
        "About"
    };


//~~OBJECT CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** User instance object */
    private final ApplicationInstance instance;

    /** Scanner from which user inputs are to be scanned */
    private final Scanner scanner;

    /** PrintStream to which interface messages are to be printed */
    private final PrintStream out;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Makes a {@code ConsoleInterface} with the system I/O streams*/
    public ConsoleInterface() {
        this(System.out, System.in);
    }

    /** Makes a {@code ConsoleInterface} with the given I/O streams */
    public ConsoleInterface(PrintStream out, InputStream in) {
        this.instance = new ApplicationInstance();
        this.scanner = new Scanner(in);
        this.out = out;
    }


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** This method is the starting point to the fallback GUI */
    public void run() {
        uiMain();
    }


//~~PRIVATE OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//~~~~USER INTERFACE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  UI: /
     */
    private void uiMain() {
        byte in;

        while (true) {
            sayHead(APPLICATION_NAME);
            sayMenu(MAIN);
            in = askChoice((byte) 0, (byte) MAIN.length);

            switch (in) {
                case 1:
                    uiFile();
                    break;
                case 2:
                    uiView();
                    break;
                case 3:
                    uiTransport();
                    break;
                case 4:
                    uiAudio();
                    break;
                case 5:
                    uiHelp();
                    break;
                case 0:
                    System.exit(0);
            }
        }
    }

    /**
     *  UI: /File
     */
    private void uiFile() {
        byte in;

        while (true) {
            sayHead("File");
            sayMenu(FILE);
            in = askChoice((byte) 0, (byte) FILE.length);

            switch (in) {
                case 1:
                    uiNew();
                    break;
                case 2:
                    uiOpen(SEQUENCE);
                    break;
                case 3:
                    uiOpen(FREEZING_SEQUENCE);
                    break;
                case 4:
                    uiSave();
                    break;
                case 5:
                    uiReload();
                    break;
                case 6:
                    uiClose();
                    break;
                case 0:
                    return;
            }
        }
    }

    /**
     *  UI: /File/New
     */
    private void uiNew() {
        if (this.instance.isModified()) {
            uiSavePrompt();
        }
        this.instance.close();
        int start, end, rate, width, height;
        sayHead("New");
        String path = askString("Input path to sequence files");
        String name = askString("Input sequence name");

        while (true) {
            start = askInteger("Input starting point");
            end   = askInteger("Input ending point");

            if (end <= start) {
                sayError("Invalid range: " +
                    "A sequence may not end (before) where it starts."
                );
                continue;
            }
            break;
        }
        while (true) {
            rate = askInteger(
                "Input advance rate"
            );
            if (!isInputWithinRange(1, Byte.MAX_VALUE, rate)) {
                sayError("Invalid rate. Range: [1, 127]");
                continue;
            }
            break;
        }
        while (true) {
            width = askInteger("Input projection width");

            if (!isInputWithinRange(1, Short.MAX_VALUE, width)) {
                sayError("Invalid width. Range: [1, 32767]");
                continue;
            }
            break;
        }
        while (true) {
            height = askInteger("Input projection height");

            if (!isInputWithinRange(1, Short.MAX_VALUE, height)) {
                sayError("Invalid height. Range: [1, 32767]");
                continue;
            }
            break;
        }
        try {
            sayWarning("W A I T ...");

            this.instance.make(
                path, name, start, end,
                rate, (short) width, (short) height
            );
            sayInfo("...MAKE SUCCESS");
        } catch (Exception e) {
            sayError("...MAKE FAILED\n" + e.toString());
        }
    }

    /**
     *  UI: /File/Open
     */
    private void uiOpen(String type) {
        if (this.instance.isModified()) {
            uiSavePrompt();
        }
        sayHead("Open");
        String in = askString("Input path");

        try {
            sayWarning("W A I T ...");
            this.instance.open(in, type);
            sayInfo("...LOAD SUCCESS");
        } catch (Exception e) {
            sayError("...LOAD FAILED\n" + e.toString());
        }
    }

    /**
     *  UI: /File/Save
     */
    private void uiSave() {
        sayHead("Save");

        if (!this.instance.isLoaded()) {
            sayError("No sequence loaded");
            return;
        }
        try {
            sayWarning("W A I T ...");
            this.instance.save();
            sayInfo("...SAVE SUCCESS");
        } catch (Exception e) {
            sayError("...SAVE FAILED: " + e.toString());
        }
    }

    /**
     *  UI: /File/Reload
     */
    private void uiReload() {
        if (!this.instance.isLoaded()) {
            sayError("No sequence loaded");
            return;
        }
        if (this.instance.isModified()) {
            uiSavePrompt();
        }
        sayHead("Reload");

        try {
            sayWarning("W A I T ...");
            this.instance.reload();
            sayInfo("...RELOAD SUCCESS");
        } catch (Exception e) {
            sayError("...RELOAD FAILED: " + e.toString());
        }
    }

    /**
     *  UI: /File/Close
     */
    private void uiClose() {
        if (!this.instance.isLoaded()) {
            sayError("No sequence loaded");
            return;
        }
        if (this.instance.isModified()) {
            uiSavePrompt();
        }
        sayHead("Close");
        this.instance.close();
        sayInfo("Sequence closed");
    }

    /**
     *  UI: /File/.SavePrompt
     */
    private void uiSavePrompt() {
        byte in;

        while (true) {
            sayQuestion(
                "Save the currently loaded sequence?\n[1] Yes  [0] No\n"
            );
            in = askChoice((byte) 0, (byte) 1);

            switch (in) {
                case 1:
                    uiSave();
                case 0:
                    return;
            }
        }
    }

    /**
     *  UI: /View
     */
    private void uiView() {
        byte in;

        while (true) {
            sayHead("View");
            sayMenu(VIEW);
            in = askChoice((byte) 0, (byte) VIEW.length);

            switch (in) {
                case 1:
                    uiSequenceInformation();
                    break;
                case 0:
                    return;
            }
        }
    }

    /**
     *  UI: /View/SequenceInformation
     */
    private void uiSequenceInformation() {
        if (!this.instance.isLoaded()) {
            sayError("No sequence loaded");
            return;
        }
        sayHead("Sequence Information");

        this.out.println(
            "Name: "    + this.instance.getName()            + "\n"    +
            "Range: ["  + this.instance.getStart()           + ", "    +
                          this.instance.getEnd()             + "], "   +
                          this.instance.getLength()          + "\n"    +
            "Rate: "    + this.instance.getRate()            + " /s\n" +
            "Length: "  + this.instance.getLengthSecond()    + "s\n"   +
            "Elapsed: " + this.instance.getElapsedSecond()   + "s ("   +
                   (100 * this.instance.getElapsedPercent()) + "%)\n"
        );
    }

    /**
     *  UI: /Transport
     */
    private void uiTransport() {
        if (!this.instance.isLoaded()) {
            sayError("No sequence loaded");
            return;
        }
        byte in;

        while (true) {
            sayHead("Transport");
            sayMenu(TRANSPORT);
            in = askChoice((byte) 0, (byte) TRANSPORT.length);

            switch (in) {
                case 1:
                    this.instance.play();
                    break;
                case 2:
                    this.instance.pause();
                    break;
                case 3:
                    this.instance.stop();
                    break;
                case 4:
                    this.instance.fastRewind();
                    break;
                case 5:
                    this.instance.fastForward();
                    break;
                case 6:
                    this.instance.stepBackward();
                    break;
                case 7:
                    this.instance.stepForward();
                    break;
                case 8:
                    this.instance.jumpToStart();
                    break;
                case 9:
                    this.instance.jumpToEnd();
                    break;
                case 10:
                    uiJumpTo();
                    break;
                case 11:
                    this.instance.trickPlay();
                    break;
                case 0:
                    return;
            }
        }
    }

    /**
     *  UI: /Transport/JumpTo
     */
    private void uiJumpTo() {
        if (!this.instance.jump(askInteger("Input frame"))) {
            sayError("Invalid frame");
        }
    }

    /**
     *  UI: /Audio
     */
    private void uiAudio() {
        if (!this.instance.isLoaded()) {
            sayError("No sequence loaded");
            return;
        }
        byte in;

        while (true) {
            sayHead("Audio");
            sayInfo("Select audio track. 0 to output all tracks.");
            in = askChoice((byte) 0, Byte.MAX_VALUE);

            if (in >= 0) {
                this.instance.setTrack(in);
                break;
            }
        }
    }

    /**
     *  UI: /Help
     */
    private void uiHelp() {
        byte in;

        while (true) {
            sayHead("Help");
            sayMenu(HELP);
            in = askChoice((byte) 0, (byte) HELP.length);

            switch (in) {
                case 1:
                    uiAbout();
                    break;
                case 0:
                    return;
            }
        }
    }

    /**
     *  UI: /Help/About
     */
    private void uiAbout() {
        sayHead("About");

        this.out.println(
            "\n\n" +
            APPLICATION_NAME + "\n" +
            "----------\n" +
            APPLICATION_VERSION + " by Brendon, " + APPLICATION_DATE + ".\n\n"
        );
    }


//~~~~MESSAGE HELPERS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Prints the given information message */
    private void sayInfo(String message) {
        this.out.println("<" + APPLICATION_NAME + "/i> " + message);
    }

    /** Prints the given warning message */
    private void sayWarning(String message) {
        this.out.println("<" + APPLICATION_NAME + "/!> " + message);
    }

    /** Prints the given error message */
    private void sayError(String message) {
        this.out.println("<" + APPLICATION_NAME + "/X> " + message);
    }

    /** Prints the given question */
    private void sayQuestion(String message) {
        this.out.println("<" + APPLICATION_NAME + "/?> " + message);
    }

    /** Prints the given header message */
    private void sayHead(String head) {
        StringBuilder builder = new StringBuilder("\n\n");
        builder.append(head);
        builder.append('\n');

        for (int i = 0; i < head.length(); i++) {
            builder.append('-');
        }
        this.out.println(builder.toString());
    }

    /** Prints the given String[] as a choice menu */
    private void sayMenu(String[] menu) {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i <= menu.length; i++) {
            builder.append('[');
            builder.append(i);
            builder.append("] ");
            builder.append(menu[i - 1]);
            builder.append('\n');
        }
        builder.append("[0] Return\n");
        this.out.println(builder.toString());
    }

    /** Prompts the user for a byte-ranged input */
    private byte askChoice(byte from, byte to) {
        return askChoice(from, to, INPUT_ATTEMPTS);
    }

    /**
     *  Prompts the user for a byte-ranged input with the given number of tries
     */
    private byte askChoice(byte from, byte to, byte tries) {
        byte in = Byte.MIN_VALUE;

        while (tries-- > 0) {
            this.out.print("<" + APPLICATION_NAME + "/?> " +
                "Input [" + from + '-' + to + "]: "
            );
            try {
                in = Byte.parseByte(scanner.nextLine());

                if (!isInputWithinRange(from, to, in)) {
                    sayError("Invalid input");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                sayError("Bad input");
            }
        }
        return in;
    }

    /** Prompts the user for a integral input with the given String */
    private int askInteger(String prompt) {
        return askInteger(prompt, INPUT_ATTEMPTS);
    }

    /**
     *  Prompts the user for a integral input with the given String and number
     *  of tries
     */
    private int askInteger(String prompt, byte tries) {
        while (tries-- > 0) {
            this.out.print("<" + APPLICATION_NAME + "/?> " + prompt + ": ");

            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                sayError("Bad input");
            }
        }
        return 0;
    }

    /** Prompts the user for a String input with the given String */
    private String askString(String prompt) {
        this.out.print("<" + APPLICATION_NAME + "/?> " + prompt + "\n>: ");
        return scanner.nextLine();
    }


//~~~~MISCELLANEOUS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Returns whether the given input is valid within the range [from, to] */
    private boolean isInputWithinRange(int from, int to, int input) {
        return (input >= from) && (input <= to);
    }
}
