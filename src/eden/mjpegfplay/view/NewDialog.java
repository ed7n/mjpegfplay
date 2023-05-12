package eden.mjpegfplay.view;

import static eden.mjpegfplay.view.UIConstants.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * A NewDialog presents a form to make new sequence metadata files.
 *
 * @author Brendon
 * @version u0r6, 05/12/2023.
 */
class NewDialog implements ActionListener {

  /** Parent application UI */
  private final ApplicationUI ui;
  /** Dialog itself */
  private final JDialog dialog;
  /** Application file chooser */
  private final JFileChooser fileChooser;
  /** Application UI LayoutManager */
  private final SpringLayout layout;
  /** Dialog labels */
  private final List<JLabel> labels = makeLabels();
  /** Dialog TextFields */
  private final List<JTextField> textFields = makeTextFields();
  /** Dialog Buttons */
  private final List<JButton> buttons = makeButtons();

  /** Makes a NewDialog with the given ApplicationUI */
  NewDialog(ApplicationUI ui) {
    this.ui = ui;
    this.dialog = new JDialog(ui.frameMain, "New Sequence Metadata", true);
    this.fileChooser = ui.chooser;
    this.layout = ui.layout;
    this.dialog.getContentPane().setBackground(COLOR_INTERFACE);
    this.dialog.getContentPane().setLayout(this.layout);
    this.dialog.setResizable(true);
    this.dialog.setSize(new Dimension(336, 280));
    initializeLabels();
    initializeTextFields();
    initializeButtons();
  }

  /** {@inheritDoc} */
  @Override
  public void actionPerformed(ActionEvent event) {
    switch (event.getActionCommand()) {
      case "browse":
        if (
          this.fileChooser.showDialog(this.dialog, "Select") ==
          JFileChooser.APPROVE_OPTION
        ) {
          this.textFields.get(6)
            .setText(
              this.fileChooser.getSelectedFile().getPath() + File.separator
            );
        }
        break;
      case "make":
        String path;
        if (!this.textFields.get(6).getText().endsWith(File.separator)) {
          path = this.textFields.get(6).getText() + File.separator;
        } else {
          path = this.textFields.get(6).getText();
        }
        try {
          this.ui.instance.make(
              path,
              this.textFields.get(0).getText(),
              Integer.parseInt(this.textFields.get(1).getText()),
              Integer.parseInt(this.textFields.get(2).getText()),
              Integer.parseInt(this.textFields.get(3).getText()),
              Short.parseShort(this.textFields.get(4).getText()),
              Short.parseShort(this.textFields.get(5).getText())
            );
        } catch (Exception exception) {
          this.ui.messenger.sayException(exception);
          break;
        }
        this.ui.messenger.sayInfo(
            "Make Sequence",
            "The metadata file has been written successfully."
          );
      case "cancel":
        this.textFields.forEach(field -> field.setText(null));
        this.dialog.setVisible(false);
        break;
    }
  }

  /** Shows this NewDialog */
  void show() {
    this.fileChooser.setDialogTitle("Save New Sequence");
    this.fileChooser.setApproveButtonText("Choose");
    this.dialog.setLocationRelativeTo(this.dialog.getParent());
    this.dialog.setVisible(true);
  }

  private void initializeLabels() {
    this.labels.forEach(label -> this.dialog.getContentPane().add(label));
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.labels.get(0),
        PADDING_VERTICAL,
        SpringLayout.NORTH,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.labels.get(0),
        0,
        SpringLayout.WEST,
        this.textFields.get(0)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.labels.get(1),
        PADDING_VERTICAL,
        SpringLayout.SOUTH,
        this.textFields.get(0)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.labels.get(1),
        0,
        SpringLayout.WEST,
        this.textFields.get(1)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.labels.get(2),
        PADDING_VERTICAL,
        SpringLayout.SOUTH,
        this.textFields.get(0)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.labels.get(2),
        0,
        SpringLayout.WEST,
        this.textFields.get(2)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.labels.get(3),
        PADDING_VERTICAL,
        SpringLayout.SOUTH,
        this.textFields.get(0)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.labels.get(3),
        0,
        SpringLayout.WEST,
        this.textFields.get(3)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.labels.get(4),
        PADDING_VERTICAL,
        SpringLayout.SOUTH,
        this.textFields.get(1)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.labels.get(4),
        0,
        SpringLayout.WEST,
        this.textFields.get(4)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.labels.get(5),
        PADDING_VERTICAL,
        SpringLayout.SOUTH,
        this.textFields.get(1)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.labels.get(5),
        0,
        SpringLayout.WEST,
        this.textFields.get(5)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.labels.get(6),
        PADDING_VERTICAL,
        SpringLayout.SOUTH,
        this.textFields.get(4)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.labels.get(6),
        0,
        SpringLayout.WEST,
        this.textFields.get(6)
      );
  }

  private void initializeTextFields() {
    this.textFields.forEach(field -> this.dialog.getContentPane().add(field));
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.textFields.get(0),
        0,
        SpringLayout.SOUTH,
        this.labels.get(0)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.textFields.get(0),
        PADDING_HORIZONTAL,
        SpringLayout.WEST,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.textFields.get(1),
        0,
        SpringLayout.SOUTH,
        this.labels.get(1)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.textFields.get(1),
        PADDING_HORIZONTAL,
        SpringLayout.WEST,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.textFields.get(2),
        0,
        SpringLayout.SOUTH,
        this.labels.get(2)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.textFields.get(2),
        PADDING_HORIZONTAL,
        SpringLayout.EAST,
        this.textFields.get(1)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.textFields.get(3),
        0,
        SpringLayout.SOUTH,
        this.labels.get(3)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.textFields.get(3),
        PADDING_HORIZONTAL,
        SpringLayout.EAST,
        this.textFields.get(2)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.textFields.get(4),
        0,
        SpringLayout.SOUTH,
        this.labels.get(4)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.textFields.get(4),
        PADDING_HORIZONTAL,
        SpringLayout.WEST,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.textFields.get(5),
        0,
        SpringLayout.SOUTH,
        this.labels.get(5)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.textFields.get(5),
        PADDING_HORIZONTAL,
        SpringLayout.EAST,
        this.textFields.get(4)
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.textFields.get(6),
        0,
        SpringLayout.SOUTH,
        this.labels.get(6)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.textFields.get(6),
        PADDING_HORIZONTAL,
        SpringLayout.WEST,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.textFields.get(6),
        -PADDING_HORIZONTAL,
        SpringLayout.WEST,
        this.buttons.get(0)
      );
  }

  private void initializeButtons() {
    this.buttons.forEach(button -> this.dialog.getContentPane().add(button));
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.buttons.get(0),
        0,
        SpringLayout.NORTH,
        this.textFields.get(6)
      );
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        this.buttons.get(0),
        0,
        SpringLayout.SOUTH,
        this.textFields.get(6)
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.buttons.get(0),
        -BUTTON_WIDTH * 2,
        SpringLayout.EAST,
        this.buttons.get(0)
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.buttons.get(0),
        -PADDING_HORIZONTAL,
        SpringLayout.EAST,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.buttons.get(1),
        -BUTTON_HEIGHT,
        SpringLayout.SOUTH,
        this.buttons.get(1)
      );
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        this.buttons.get(1),
        -PADDING_VERTICAL,
        SpringLayout.SOUTH,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.buttons.get(1),
        -BUTTON_WIDTH * 2,
        SpringLayout.EAST,
        this.buttons.get(1)
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.buttons.get(1),
        -PADDING_HORIZONTAL,
        SpringLayout.EAST,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.NORTH,
        this.buttons.get(2),
        -BUTTON_HEIGHT,
        SpringLayout.SOUTH,
        this.buttons.get(2)
      );
    this.layout.putConstraint(
        SpringLayout.SOUTH,
        this.buttons.get(2),
        -PADDING_VERTICAL,
        SpringLayout.SOUTH,
        this.dialog.getContentPane()
      );
    this.layout.putConstraint(
        SpringLayout.WEST,
        this.buttons.get(2),
        -BUTTON_WIDTH * 2,
        SpringLayout.EAST,
        this.buttons.get(2)
      );
    this.layout.putConstraint(
        SpringLayout.EAST,
        this.buttons.get(2),
        -PADDING_HORIZONTAL,
        SpringLayout.WEST,
        this.buttons.get(1)
      );
  }

  List<JLabel> makeLabels() {
    List<JLabel> out = new ArrayList<>(7);
    out.add(new JLabel("Name:"));
    out.add(new JLabel("Start:"));
    out.add(new JLabel("End:"));
    out.add(new JLabel("Rate:"));
    out.add(new JLabel("Width:"));
    out.add(new JLabel("Height:"));
    out.add(new JLabel("Path:"));
    out.forEach(label -> label.setFont(FONT_DIALOG));
    return Collections.unmodifiableList(out);
  }

  private List<JTextField> makeTextFields() {
    List<JTextField> out = new ArrayList<>(7);
    out.add(new JTextField(20));
    out.add(new JTextField(7));
    out.add(new JTextField(7));
    out.add(new JTextField(4));
    out.add(new JTextField(7));
    out.add(new JTextField(7));
    out.add(new JTextField(20));
    out.stream().forEach(field -> field.setFont(FONT_TEXT_FIELD));
    return Collections.unmodifiableList(out);
  }

  private List<JButton> makeButtons() {
    List<JButton> out = new ArrayList<>(3);
    out.add(new InterfaceButton("Browse…", "browse", this));
    out.add(new InterfaceButton("Cancel", "cancel", this));
    out.add(new InterfaceButton("Make", "make", this));
    return Collections.unmodifiableList(out);
  }
}
