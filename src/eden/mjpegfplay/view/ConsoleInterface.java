package eden.mjpegfplay.view;

import static eden.common.shared.Constants.EOL;
import static eden.common.shared.Constants.SPACE;
import static eden.mjpegfplay.model.ApplicationInformation.*;
import static eden.mjpegfplay.model.SequenceTypes.*;

import eden.mjpegfplay.presenter.ApplicationInstance;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * This class represents the fallback view in the model-view-presenter
 * architectural pattern of this application.
 *
 * @author Brendon
 * @version u0r5, 05/05/2023.
 */
public class ConsoleInterface implements Runnable {

  /** Default number of input attempts */
  public static final byte INPUT_ATTEMPTS = 3;
  private static final String[] MAIN = new String[] {
    "File",
    "View",
    "Transport",
    "Audio",
    "Help",
  };
  private static final String[] FILE = new String[] {
    "New...",
    "Load...",
    "Load Freezing",
    "Load Music...",
    "Reload",
    "Eject",
  };
  private static final String[] VIEW = new String[] { "Sequence Information" };
  private static final String[] TRANSPORT = new String[] {
    "-> Play",
    "|| Pause",
    "[] Stop",
    "<< Fast Rewind",
    ">> Fast Forward",
    "<| Step Backward",
    "|> Step Forward",
    "|< Jump To Start",
    ">| Jump To End",
    "># Jump To...",
    "<- Trickplay",
    "Set Lens Count...",
  };
  private static final String[] HELP = new String[] { "About" };
  private static final String ERROR_NO_SEQUENCE = "No sequence.";
  private static final String DONE = "Done.";
  /** User instance object */
  private final ApplicationInstance instance;
  /** Scanner from which user inputs are to be scanned */
  private final Scanner scanner;
  /** PrintStream to which interface messages are to be printed */
  private final PrintStream out;

  /** Makes a {@code ConsoleInterface} with the system I/O streams */
  public ConsoleInterface() {
    this(System.out, System.in);
  }

  /** Makes a {@code ConsoleInterface} with the given I/O streams */
  public ConsoleInterface(PrintStream out, InputStream in) {
    this.instance = new ApplicationInstance();
    this.scanner = new Scanner(in);
    this.out = out;
  }

  /** This method is the starting point to the fallback GUI */
  @Override
  public void run() {
    uiMain();
  }

  /**
   * UI: /
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
   * UI: /File
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
          uiLoad(SEQUENCE);
          break;
        case 3:
          uiLoad(FREEZING_SEQUENCE);
          break;
        case 4:
          uiLoad(MUSIC_SEQUENCE);
          break;
        case 5:
          uiReload();
          break;
        case 6:
          uiEject();
          break;
        case 7:
          return;
      }
    }
  }

  /**
   * UI: /File/New
   */
  private void uiNew() {
    int start, end, rate, width, height;
    sayHead("New");
    String path = askString("Enter path to sequence files.");
    String name = askString("Enter sequence name.");
    while (true) {
      start = askInteger("Enter starting point.");
      end = askInteger("Enter ending point.");
      if (end <= start) {
        sayError(
          "Invalid range: A sequence may not end on or before where it starts."
        );
        continue;
      }
      break;
    }
    while (true) {
      rate = askInteger("Enter advance rate [1, 127].");
      if (!isInputWithinRange(1, Byte.MAX_VALUE, rate)) {
        sayError("Invalid rate.");
        continue;
      }
      break;
    }
    while (true) {
      width = askInteger("Enter projection width [1, 32767].");
      if (!isInputWithinRange(1, Short.MAX_VALUE, width)) {
        sayError("Invalid width.");
        continue;
      }
      break;
    }
    while (true) {
      height = askInteger("Enter projection height [1, 32767].");
      if (!isInputWithinRange(1, Short.MAX_VALUE, height)) {
        sayError("Invalid height.");
        continue;
      }
      break;
    }
    try {
      sayInfo("Now making.");
      this.instance.make(
          path,
          name,
          start,
          end,
          rate,
          (short) width,
          (short) height
        );
      sayInfo(DONE);
    } catch (Exception exception) {
      sayError(exception.toString());
    }
  }

  /**
   * UI: /File/Load
   */
  private void uiLoad(String type) {
    if (this.instance.isModified()) {
      uiSavePrompt();
    }
    sayHead("Load");
    String in = askString("Enter path.");
    if (!in.endsWith(File.separator)) {
      in += File.separator;
    }
    try {
      sayInfo("Now loading.");
      this.instance.open(in, type);
      sayInfo(DONE);
    } catch (Exception exception) {
      sayError(exception.toString());
    }
  }

  /**
   * UI: /File/Save
   */
  private void uiSave() {
    sayHead("Save");
    if (!this.instance.isLoaded()) {
      sayError(ERROR_NO_SEQUENCE);
      return;
    }
    try {
      sayInfo("Now saving.");
      this.instance.save();
      sayInfo(DONE);
    } catch (Exception exception) {
      sayError(exception.toString());
    }
  }

  /**
   * UI: /File/Reload
   */
  private void uiReload() {
    if (!this.instance.isLoaded()) {
      sayError(ERROR_NO_SEQUENCE);
      return;
    }
    if (this.instance.isModified()) {
      uiSavePrompt();
    }
    sayHead("Reload");
    try {
      sayInfo("Now reloading.");
      this.instance.reload();
      sayInfo(DONE);
    } catch (Exception exception) {
      sayError(exception.toString());
    }
  }

  /**
   * UI: /File/Close
   */
  private void uiEject() {
    if (!this.instance.isLoaded()) {
      sayError(ERROR_NO_SEQUENCE);
      return;
    }
    if (this.instance.isModified()) {
      uiSavePrompt();
    }
    sayHead("Eject");
    this.instance.close();
    sayInfo("Sequence ejected.");
  }

  /**
   * UI: /File/SavePrompt
   */
  private void uiSavePrompt() {
    byte in;
    while (true) {
      sayQuestion("Save the current sequence?" + EOL + "[1] Yes  [0] No" + EOL);
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
   * UI: /View
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
   * UI: /View/SequenceInformation
   */
  private void uiSequenceInformation() {
    if (!this.instance.isLoaded()) {
      sayError(ERROR_NO_SEQUENCE);
      return;
    }
    sayHead("Sequence Information");
    this.out.println(
        "Name: " +
        this.instance.getName() +
        EOL +
        "Range: [" +
        this.instance.getStart() +
        ", " +
        this.instance.getEnd() +
        "], " +
        this.instance.getLength() +
        EOL +
        "Rate: " +
        this.instance.getRate() +
        "/s" +
        EOL +
        "Length: " +
        this.instance.getLengthSecond() +
        "s" +
        EOL +
        "Elapsed: " +
        this.instance.getElapsedSecond() +
        "s (" +
        this.instance.getElapsedPercent() *
        100 +
        "%)" +
        EOL
      );
  }

  /**
   * UI: /Transport
   */
  private void uiTransport() {
    byte in;
    while (true) {
      sayHead("Transport");
      sayMenu(TRANSPORT);
      in = askChoice((byte) 0, (byte) TRANSPORT.length);
      if (in > 0 && in < 12) {
        continue;
      }
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
        case 12:
          uiSetLensCount();
          break;
        case 0:
          return;
      }
    }
  }

  /**
   * UI: /Transport/JumpTo
   */
  private void uiJumpTo() {
    if (!this.instance.jump(askInteger("Enter frame number."))) {
      sayError("Invalid frame number.");
    }
  }

  /**
   * UI: /Transport/SetLensCount
   */
  private void uiSetLensCount() {
    int in = askInteger("Enter render lens count [1, 9].");
    if (in < 1 || in > 9) {
      sayError("Invalid count.");
      return;
    }
    this.instance.setLensCount((byte) in);
  }

  /**
   * UI: /Audio
   */
  private void uiAudio() {
    if (!this.instance.isLoaded()) {
      sayError(ERROR_NO_SEQUENCE);
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
   * UI: /Help
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
   * UI: /Help/About
   */
  private void uiAbout() {
    sayHead("About");
    this.out.print(
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
        EOL
      );
  }

  /** Prints the given information message */
  private void sayInfo(String message) {
    this.out.println("[" + APPLICATION_NAME + "/i] " + message);
  }

  /** Prints the given warning message */
  private void sayWarning(String message) {
    this.out.println("[" + APPLICATION_NAME + "/!] " + message);
  }

  /** Prints the given error message */
  private void sayError(String message) {
    this.out.println("[" + APPLICATION_NAME + "/X] " + message);
  }

  /** Prints the given question */
  private void sayQuestion(String message) {
    this.out.println("[" + APPLICATION_NAME + "/?] " + message);
  }

  /** Prints the given header message */
  private void sayHead(String head) {
    this.out.println(new StringBuilder(head).append(":").toString());
  }

  /** Prints the given String[] as a choice menu */
  private void sayMenu(String[] menu) {
    StringBuilder builder = new StringBuilder();
    for (int index = 1; index <= menu.length; index++) {
      builder
        .append('[')
        .append(index)
        .append("] ")
        .append(menu[index - 1])
        .append(EOL);
    }
    this.out.println(builder.append("[0] Return").toString());
  }

  /** Prompts the user for a byte-ranged input */
  private byte askChoice(byte from, byte to) {
    return askChoice(from, to, INPUT_ATTEMPTS);
  }

  /**
   * Prompts the user for a byte-ranged input with the given number of tries
   */
  private byte askChoice(byte from, byte to, byte tries) {
    byte in = Byte.MIN_VALUE;
    while (tries-- > 0) {
      this.out.print("> ");
      try {
        in = Byte.parseByte(scanner.nextLine());
        if (!isInputWithinRange(from, to, in)) {
          sayError("Invalid input.");
          continue;
        }
        break;
      } catch (NumberFormatException exception) {
        sayError("Bad input.");
      }
    }
    return in;
  }

  /** Prompts the user for a integral input with the given String */
  private int askInteger(String prompt) {
    return askInteger(prompt, INPUT_ATTEMPTS);
  }

  /**
   * Prompts the user for a integral input with the given String and number of
   * tries
   */
  private int askInteger(String prompt, byte tries) {
    while (tries-- > 0) {
      this.out.print("[" + APPLICATION_NAME + "/?] " + prompt + EOL + "> ");
      try {
        return Integer.parseInt(scanner.nextLine());
      } catch (NumberFormatException exception) {
        sayError("Bad input.");
      }
    }
    return 0;
  }

  /** Prompts the user for a String input with the given String */
  private String askString(String prompt) {
    this.out.print("[" + APPLICATION_NAME + "/?] " + prompt + EOL + "> ");
    return scanner.nextLine();
  }

  /** Returns whether the given input is valid within the range [from, to] */
  private boolean isInputWithinRange(int from, int to, int input) {
    return (input >= from) && (input <= to);
  }
}
