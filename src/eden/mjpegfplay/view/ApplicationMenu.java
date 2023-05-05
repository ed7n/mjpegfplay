package eden.mjpegfplay.view;

import static eden.mjpegfplay.view.UIConstants.*;
import static eden.mjpegfplay.view.UserCommands.*;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * An ApplicationMenu represents a grouped list of available user commands to
 * this application.
 *
 * @author Brendon
 * @version u0r5, 05/05/2023.
 */
class ApplicationMenu {

  private final JMenuBar menuBar = new JMenuBar();
  private final JMenu menuFile = new JMenu("File");
  private final JMenu menuView = new JMenu("View");
  private final JMenu menuTransport = new JMenu("Transport");
  private final JMenu menuAudio = new JMenu("Audio");
  private final JMenu menuHelp = new JMenu("Help");
  private final List<JMenuItem> itemsFile;
  private final List<JMenuItem> itemsView;
  private final List<JMenuItem> itemsTransport;
  private final List<JMenuItem> itemsAudio;
  private final List<JMenuItem> itemsHelp;

  /** Makes an ApplicationMenu with the given ActionListener */
  ApplicationMenu(ActionListener listener) {
    this.itemsFile = makeFile(listener);
    this.itemsView = makeView(listener);
    this.itemsTransport = makeTransport(listener);
    this.itemsAudio = makeAudio(listener);
    this.itemsHelp = makeHelp(listener);
    this.menuBar.setBackground(COLOR_MENU);
    this.menuBar.setBorder(null);
    this.menuBar.setFont(FONT_MENU);
    this.menuBar.setName("Application Menu");
    this.menuBar.add(this.menuFile);
    this.menuBar.add(this.menuView);
    this.menuBar.add(this.menuTransport);
    this.menuBar.add(this.menuAudio);
    this.menuBar.add(this.menuHelp);
    initializeFile();
    initializeView();
    initializeTransport();
    initializeAudio();
    initializeHelp();
  }

  /** Returns the JMenuBar of this ApplicationMenu */
  JMenuBar getMenuBar() {
    return this.menuBar;
  }

  private void initializeFile() {
    this.menuFile.add(this.itemsFile.get(0));
    this.menuFile.addSeparator();
    this.menuFile.add(this.itemsFile.get(1));
    this.menuFile.add(this.itemsFile.get(2));
    this.menuFile.add(this.itemsFile.get(3));
    this.menuFile.addSeparator();
    this.menuFile.add(this.itemsFile.get(4));
    this.menuFile.addSeparator();
    this.menuFile.add(this.itemsFile.get(5));
    this.menuFile.addSeparator();
    this.menuFile.add(this.itemsFile.get(6));
    this.menuFile.setMnemonic(KeyEvent.VK_F);
    decorateMenu(this.menuFile);
  }

  private void initializeView() {
    this.menuView.add(this.itemsView.get(0));
    this.menuView.add(this.itemsView.get(1));
    this.menuView.addSeparator();
    this.menuView.add(this.itemsView.get(2));
    this.menuView.add(this.itemsView.get(3));
    this.menuView.setMnemonic(KeyEvent.VK_V);
    decorateMenu(this.menuView);
  }

  private void initializeTransport() {
    this.menuTransport.add(this.itemsTransport.get(0));
    this.menuTransport.add(this.itemsTransport.get(1));
    this.menuTransport.add(this.itemsTransport.get(2));
    this.menuTransport.addSeparator();
    this.menuTransport.add(this.itemsTransport.get(3));
    this.menuTransport.add(this.itemsTransport.get(4));
    this.menuTransport.addSeparator();
    this.menuTransport.add(this.itemsTransport.get(5));
    this.menuTransport.add(this.itemsTransport.get(6));
    this.menuTransport.addSeparator();
    this.menuTransport.add(this.itemsTransport.get(7));
    this.menuTransport.add(this.itemsTransport.get(8));
    this.menuTransport.add(this.itemsTransport.get(9));
    this.menuTransport.addSeparator();
    this.menuTransport.add(this.itemsTransport.get(10));
    this.menuTransport.addSeparator();
    this.menuTransport.add(this.itemsTransport.get(11));
    this.menuTransport.setMnemonic(KeyEvent.VK_T);
    decorateMenu(this.menuTransport);
  }

  private void initializeAudio() {
    this.menuAudio.add(this.itemsAudio.get(0));
    this.menuAudio.add(this.itemsAudio.get(1));
    this.menuAudio.add(this.itemsAudio.get(2));
    this.menuAudio.addSeparator();
    this.menuAudio.add(this.itemsAudio.get(3));
    this.menuAudio.add(this.itemsAudio.get(4));
    this.menuAudio.addSeparator();
    this.menuAudio.add(this.itemsAudio.get(5));
    this.menuAudio.add(this.itemsAudio.get(6));
    this.menuAudio.addSeparator();
    this.menuAudio.add(this.itemsAudio.get(7));
    this.menuAudio.add(this.itemsAudio.get(8));
    this.menuAudio.add(this.itemsAudio.get(9));
    this.menuAudio.setMnemonic(KeyEvent.VK_A);
    decorateMenu(this.menuAudio);
  }

  private void initializeHelp() {
    this.itemsHelp.forEach(i -> this.menuHelp.add(i));
    this.menuHelp.setMnemonic(KeyEvent.VK_H);
    decorateMenu(this.menuHelp);
  }

  private void decorateMenu(JMenu menu) {
    menu.setFont(FONT_MENU);
  }

  private List<JMenuItem> makeFile(ActionListener listener) {
    List<JMenuItem> out = new ArrayList<>(7);
    out.add(new CommandMenuItem("New…", F_NEW, listener));
    out.add(new CommandMenuItem("Load…", F_LOAD, listener));
    out.add(new CommandMenuItem("Load Freezing…", F_LOAD_FREEZING, listener));
    out.add(new CommandMenuItem("Load Music…", F_LOAD_MUSIC, listener));
    out.add(new CommandMenuItem("Reload", F_RELOAD, listener));
    out.add(new CommandMenuItem("Eject", F_EJECT, listener));
    out.add(new CommandMenuItem("Quit", F_QUIT, listener));
    return Collections.unmodifiableList(out);
  }

  private List<JMenuItem> makeView(ActionListener listener) {
    List<JMenuItem> out = new ArrayList<>(4);
    out.add(new CommandMenuItem("Multi-Window", V_MULTI_WINDOW, listener));
    out.add(new CommandMenuItem("Single-Window", V_SINGLE_WINDOW, listener));
    out.add(
      new CommandMenuItem(
        "Sequence Information",
        V_SEQUENCE_INFORMATION,
        listener
      )
    );
    out.add(
      new CommandMenuItem("Render Statistics", V_RENDER_STATISTICS, listener)
    );
    return Collections.unmodifiableList(out);
  }

  private List<JMenuItem> makeTransport(ActionListener listener) {
    List<JMenuItem> out = new ArrayList<>(12);
    out.add(new CommandMenuItem("Play", T_PLAY, listener));
    out.add(new CommandMenuItem("Pause", T_PAUSE, listener));
    out.add(new CommandMenuItem("Stop", T_STOP, listener));
    out.add(new CommandMenuItem("Fast Rewind", T_FAST_REWIND, listener));
    out.add(new CommandMenuItem("Fast Forward", T_FAST_FORWARD, listener));
    out.add(new CommandMenuItem("Step Backward", T_STEP_BACKWARD, listener));
    out.add(new CommandMenuItem("Step Forward", T_STEP_FORWARD, listener));
    out.add(new CommandMenuItem("Jump To Start", T_JUMP_TO_START, listener));
    out.add(new CommandMenuItem("Jump To End", T_JUMP_TO_END, listener));
    out.add(new CommandMenuItem("Jump To Frame…", T_JUMP_TO_FRAME, listener));
    out.add(new CommandMenuItem("Trickplay", T_TRICKPLAY, listener));
    out.add(new CommandMenuItem("Set Lens Count…", T_SET_LENSCOUNT, listener));
    return Collections.unmodifiableList(out);
  }

  private List<JMenuItem> makeAudio(ActionListener listener) {
    List<JMenuItem> out = new ArrayList<>(10);
    out.add(new CommandMenuItem("Mute", A_MUTE, listener));
    out.add(new CommandMenuItem("Unmute", A_UNMUTE, listener));
    out.add(new CommandMenuItem("Toggle Mute", A_TOGGLE_MUTE, listener));
    out.add(new CommandMenuItem("Volume 2.0x", A_VOLUME + "200", listener));
    out.add(new CommandMenuItem("Volume 1.5x", A_VOLUME + "150", listener));
    out.add(new CommandMenuItem("Volume 100%", A_VOLUME + "100", listener));
    out.add(new CommandMenuItem("Volume 1/2 ", A_VOLUME + "050", listener));
    out.add(new CommandMenuItem("Track 1", A_TRACK + "001", listener));
    out.add(new CommandMenuItem("Track 2", A_TRACK + "002", listener));
    out.add(new CommandMenuItem("Mix All", A_TRACK + "000", listener));
    return Collections.unmodifiableList(out);
  }

  private List<JMenuItem> makeHelp(ActionListener listener) {
    List<JMenuItem> out = new ArrayList<>(1);
    out.add(new CommandMenuItem("About", H_ABOUT, listener));
    return Collections.unmodifiableList(out);
  }
}
