package eden.mjpegfplay.view;

import static eden.common.shared.Constants.SPACE;
import static eden.mjpegfplay.model.ApplicationInformation.*;
import static eden.mjpegfplay.view.UIConstants.*;
import static eden.mjpegfplay.view.UserCommands.*;

import eden.common.video.CSSColor;
import eden.mjpegfplay.presenter.ApplicationInstance;
import java.util.ArrayList;
import java.util.Collections;
// Required by JSlider.
import java.util.Hashtable;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * This class hides the construction of an {@code ApplicationUI}.
 *
 * @author Brendon
 * @version u0r5, 05/05/2023.
 *
 * @see ApplicationUI
 */
public class ApplicationUIMaker extends ApplicationUI {

  /**
   * Makes an {@code ApplicationUI} with the given {@code ApplicationInstance}
   */
  public ApplicationUIMaker(ApplicationInstance instance) {
    this.instance = instance;
    this.labels = makeLabels();
    this.buttons = makeButtons();
    this.slider = makeSlider();
    this.spinner = makeSpinner();
    initialize();
  }

  private void initialize() {
    TransportKeyListener listener = new TransportKeyListener();
    this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    this.chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
    this.chooser.setMultiSelectionEnabled(false);
    this.frameMain.addKeyListener(listener);
    this.frameMain.setName("Application Main Frame");
    this.frameMain.setBackground(CSSColor.BLACK);
    this.frameMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.frameMain.setTitle(APPLICATION_NAME + SPACE + APPLICATION_VERSION);
    this.frameMain.setMinimumSize(SIZE_FRAME);
    this.frameMain.setJMenuBar(this.menu.getMenuBar());
    this.frameMain.setLayout(this.layout);
    this.frameFloat.addKeyListener(listener);
    this.frameFloat.addWindowListener(new FloatingWindowListener());
    this.frameFloat.setName("Application Floating Frame");
    this.frameFloat.setBackground(CSSColor.BLACK);
    this.frameFloat.setTitle(
        "Render - " + APPLICATION_NAME + SPACE + APPLICATION_VERSION
      );
    initializeContainers();
    this.frameMain.validate();
    this.frameMain.pack();
    this.frameMain.toFront();
    this.frameMain.setVisible(true);
  }

  private void initializeContainers() {
    this.frameMain.getContentPane().add(this.panelRenderer);
    this.frameMain.getContentPane().add(this.panelInterface);
    this.frameMain.getContentPane().setBackground(CSSColor.BLACK);
    this.frameMain.getContentPane().setName("Application Main Pane");
    this.frameMain.getContentPane().setLayout(this.layout);
    this.frameMain.getContentPane().setPreferredSize(SIZE_FRAME);
    this.frameFloat.getContentPane().setBackground(CSSColor.BLACK);
    this.frameFloat.getContentPane().setName("Application Floating Pane");
    this.frameFloat.getContentPane().setPreferredSize(SIZE_RENDER);
    initializePanelRender();
    initializePanelInterface();
  }

  private void initializePanelRender() {
    this.panelRenderer.setBackground(CSSColor.BLACK);
    this.panelRenderer.setName("Renderer Panel");
    this.panelRenderer.setLayout(this.layout);
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.panelRenderer,
        0,
        SpringLayout.NORTH,
        this.frameMain.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        this.panelRenderer,
        -160,
        SpringLayout.SOUTH,
        this.frameMain.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.panelRenderer,
        0,
        SpringLayout.WEST,
        this.frameMain.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.panelRenderer,
        0,
        SpringLayout.EAST,
        this.frameMain.getContentPane()
      );
    initializeBackdrop();
  }

  private void initializeBackdrop() {
    this.panelRenderer.add(BackdropComponent.INSTANCE);
    this.layout.putConstraint(
        SpringLayout.NORTH,
        BackdropComponent.INSTANCE,
        0,
        SpringLayout.NORTH,
        this.panelRenderer
      );
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        BackdropComponent.INSTANCE,
        0,
        SpringLayout.SOUTH,
        this.panelRenderer
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        BackdropComponent.INSTANCE,
        0,
        SpringLayout.WEST,
        this.panelRenderer
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        BackdropComponent.INSTANCE,
        0,
        SpringLayout.EAST,
        this.panelRenderer
      );
  }

  private void initializePanelInterface() {
    this.panelInterface.setBackground(COLOR_INTERFACE);
    this.panelInterface.setName("Interface Panel");
    this.panelInterface.setLayout(this.layout);
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.panelInterface,
        0,
        SpringLayout.SOUTH,
        this.panelRenderer
      );
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        this.panelInterface,
        0,
        SpringLayout.SOUTH,
        this.frameMain.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.panelInterface,
        0,
        SpringLayout.WEST,
        this.frameMain.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.panelInterface,
        0,
        SpringLayout.EAST,
        this.frameMain.getContentPane()
      );
    initializeLabels();
    initializeButtons();
    initializeSlider();
    initializeSpinner();
    initializePanel();
  }

  private void initializeLabels() {
    this.labels.forEach(label -> this.panelInterface.add(label));
    for (byte index = 0; index < 7; index++) {
      this.layout.putConstraint(
          SpringLayout.SOUTH,
          this.labels.get(index),
          0,
          SpringLayout.NORTH,
          index > 4 ? this.buttons.get(6) : this.buttons.get(0)
        );
      if (index < 4) {
        this.layout.putConstraint(
            SpringLayout.HORIZONTAL_CENTER,
            this.labels.get(index),
            0,
            SpringLayout.HORIZONTAL_CENTER,
            this.buttons.get(index)
          );
      } else if (index == 6) {
        this.layout.putConstraint(
            SpringLayout.HORIZONTAL_CENTER,
            this.labels.get(index),
            0,
            SpringLayout.HORIZONTAL_CENTER,
            this.buttons.get(9)
          );
      }
      if (4 + index >= 7) {
        continue;
      }
      this.layout.putConstraint(
          SpringLayout.HORIZONTAL_CENTER,
          this.labels.get(4 + index),
          0,
          SpringLayout.EAST,
          this.buttons.get(4 + (2 * index))
        );
    }
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        this.labels.get(7),
        0,
        SpringLayout.NORTH,
        this.slider
      );
    this.layout.putConstraint(
        SpringLayout.HORIZONTAL_CENTER,
        this.labels.get(7),
        0,
        SpringLayout.HORIZONTAL_CENTER,
        this.slider
      );
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        this.labels.get(8),
        0,
        SpringLayout.NORTH,
        this.spinner
      );
    this.layout.putConstraint(
        SpringLayout.HORIZONTAL_CENTER,
        this.labels.get(8),
        0,
        SpringLayout.HORIZONTAL_CENTER,
        this.spinner
      );
  }

  private void initializeButtons() {
    this.buttons.forEach(button -> this.panelInterface.add(button));
    for (byte index = 0; index < 6; index++) {
      this.layout.putConstraint(
          SpringLayout.NORTH,
          this.buttons.get(index),
          -160 + (PADDING_VERTICAL * 2),
          SpringLayout.SOUTH,
          this.panelInterface
        );
      this.layout.putConstraint(
          SpringLayout.SOUTH,
          this.buttons.get(index),
          BUTTON_HEIGHT,
          SpringLayout.NORTH,
          this.buttons.get(index)
        );
      this.layout.putConstraint(
          SpringLayout.WEST,
          this.buttons.get(index),
          index == 0 ? PADDING_HORIZONTAL : 0,
          index == 0 ? SpringLayout.WEST : SpringLayout.EAST,
          index == 0
            ? this.frameMain.getContentPane()
            : this.buttons.get(index - 1)
        );
      this.layout.putConstraint(
          SpringLayout.EAST,
          this.buttons.get(index),
          index == 1 ? BUTTON_WIDTH * 2 : BUTTON_WIDTH,
          SpringLayout.WEST,
          this.buttons.get(index)
        );
    }
    for (byte index = 6; index < 12; index++) {
      this.layout.putConstraint(
          SpringLayout.NORTH,
          this.buttons.get(index),
          PADDING_HORIZONTAL * 2,
          SpringLayout.SOUTH,
          this.buttons.get(0)
        );
      this.layout.putConstraint(
          SpringLayout.SOUTH,
          this.buttons.get(index),
          BUTTON_HEIGHT,
          SpringLayout.NORTH,
          this.buttons.get(index)
        );
      this.layout.putConstraint(
          SpringLayout.WEST,
          this.buttons.get(index),
          index == 6 ? PADDING_HORIZONTAL : 0,
          index == 6 ? SpringLayout.WEST : SpringLayout.EAST,
          index == 6
            ? this.frameMain.getContentPane()
            : this.buttons.get(index - 1)
        );
      this.layout.putConstraint(
          SpringLayout.EAST,
          this.buttons.get(index),
          BUTTON_WIDTH,
          SpringLayout.WEST,
          this.buttons.get(index)
        );
    }
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.buttons.get(8),
        BUTTON_WIDTH / 2,
        SpringLayout.EAST,
        this.buttons.get(7)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.buttons.get(11),
        BUTTON_WIDTH / 2,
        SpringLayout.EAST,
        this.buttons.get(10)
      );
  }

  private void initializeSlider() {
    this.panelInterface.add(this.slider);
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.slider,
        PADDING_HORIZONTAL * 2,
        SpringLayout.SOUTH,
        this.buttons.get(6)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.slider,
        PADDING_HORIZONTAL,
        SpringLayout.WEST,
        this.panelInterface
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.slider,
        SLIDER_WIDTH,
        SpringLayout.WEST,
        this.slider
      );
  }

  private void initializeSpinner() {
    this.panelInterface.add(this.spinner);
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.spinner,
        0,
        SpringLayout.NORTH,
        this.slider
      );
    this.layout.putConstraint(
        SpringLayout.HORIZONTAL_CENTER,
        this.spinner,
        0,
        SpringLayout.HORIZONTAL_CENTER,
        this.buttons.get(9)
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.spinner,
        BUTTON_WIDTH,
        SpringLayout.WEST,
        this.buttons.get(9)
      );
  }

  private void initializePanel() {
    this.panelInterface.add(this.frontPanel);
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.frontPanel,
        0,
        SpringLayout.NORTH,
        this.buttons.get(0)
      );
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        this.frontPanel,
        PANEL_HEIGHT,
        SpringLayout.NORTH,
        this.frontPanel
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.frontPanel,
        -PANEL_WIDTH - PADDING_HORIZONTAL,
        SpringLayout.EAST,
        this.panelInterface
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.frontPanel,
        PANEL_WIDTH,
        SpringLayout.WEST,
        this.frontPanel
      );
  }

  private List<JLabel> makeLabels() {
    List<JLabel> out = new ArrayList<>(9);
    out.add(new InterfaceLabel("TRICK"));
    out.add(new InterfaceLabel("PLAY / RESUME"));
    out.add(new InterfaceLabel("PAUSE"));
    out.add(new InterfaceLabel("STOP"));
    out.add(new InterfaceLabel("SCAN"));
    out.add(new InterfaceLabel("STEP"));
    out.add(new InterfaceLabel("JUMP"));
    out.add(new InterfaceLabel("OUTPUT LEVEL"));
    out.add(new InterfaceLabel("TRACK"));
    return Collections.unmodifiableList(out);
  }

  private List<JButton> makeButtons() {
    List<JButton> out = new ArrayList<>(12);
    out.add(new InterfaceButton("<", T_TRICKPLAY, this));
    out.add(new InterfaceButton(">", T_PLAY, this));
    out.add(new InterfaceButton("||", T_PAUSE, this));
    out.add(new InterfaceButton("[]", T_STOP, this));
    out.add(new InterfaceButton("<<", T_FAST_REWIND, this, "Fast Rewind"));
    out.add(new InterfaceButton(">>", T_FAST_FORWARD, this, "Fast Forward"));
    out.add(new InterfaceButton("<|", T_STEP_BACKWARD, this, "Step Backward"));
    out.add(new InterfaceButton("|>", T_STEP_FORWARD, this, "Step Forward"));
    out.add(new InterfaceButton("|<", T_JUMP_TO_START, this, "Jump To Start"));
    out.add(new InterfaceButton(">#", T_JUMP_TO_FRAME, this, "Jump To Frame…"));
    out.add(new InterfaceButton(">|", T_JUMP_TO_END, this, "Jump To End"));
    out.add(new InterfaceButton("/\\", F_EJECT, this, "Eject"));
    return Collections.unmodifiableList(out);
  }

  private JSlider makeSlider() {
    JSlider out = new JSlider(SwingConstants.HORIZONTAL, 0, 200, 100);
    Hashtable<Integer, JLabel> labels = new Hashtable<>();
    labels.put(0, new InterfaceLabel("0"));
    labels.put(50, new InterfaceLabel("1/2"));
    labels.put(100, new InterfaceLabel("100"));
    labels.put(200, new InterfaceLabel("2X"));
    out.addChangeListener(this);
    out.setBackground(null);
    out.setLabelTable(labels);
    out.setPaintLabels(true);
    return out;
  }

  private JSpinner makeSpinner() {
    // ack: https://stackoverflow.com/a/2902275
    JSpinner out = new JSpinner(
      new SpinnerNumberModel(1, 1, Byte.MAX_VALUE, 1)
    );
    out.addChangeListener(this);
    out.setBackground(COLOR_INTERFACE);
    out.setFont(FONT_BUTTON);
    ((DefaultEditor) out.getEditor()).getTextField().setEditable(false);
    return out;
  }
}
