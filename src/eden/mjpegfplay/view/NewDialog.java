// @formatter:off
package eden.mjpegfplay.view;

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
import javax.swing.SwingConstants;

import static eden.mjpegfplay.view.UIConstants.*;


/**
 *  A NewDialog presents a form to make new sequence metadata files.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
class NewDialog implements ActionListener {

//~~OBJECT CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Makes a NewDialog with the given ApplicationUI */
    NewDialog(ApplicationUI ui) {
        this.ui          = ui;

        this.dialog      = new JDialog(
            ui.frameMain, "New Sequence Metadata", true
        );
        this.fileChooser = ui.chooser;
        this.layout      = ui.layout;
        this.dialog.getContentPane().setBackground(COLOR_INTERFACE);
        this.dialog.getContentPane().setLayout(this.layout);
        this.dialog.setResizable(false);
        this.dialog.setSize(new Dimension(340, 280));
        initializeLabels();
        initializeTextFields();
        initializeButtons();
    }


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public void actionPerformed(ActionEvent event) {
        switch (event.getActionCommand()) {
            case "browse":
                if (this.fileChooser.showDialog(this.dialog, "Select") ==
                    JFileChooser.APPROVE_OPTION)
                {
                    this.textFields.get(6).setText(
                        this.fileChooser.getSelectedFile().getPath() +
                        File.separator
                    );
                }
                break;
            case "make":
                String path;

                if (!this.textFields.get(6).getText().endsWith(File.separator))
                {
                    path = this.textFields.get(6).getText() + File.separator;
                }
                else
                {
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
                } catch (Exception e) {
                    this.ui.messenger.sayException(e);
                    break;
                }
                this.ui.messenger.sayInfo("Make Sequence",
                    "The metadata file has been written successfully."
                );
            case "cancel":
                for (JTextField f : this.textFields) {
                    f.setText(null);
                }
                this.dialog.setVisible(false);
                break;
        }
    }


//~~OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Shows this NewDialog */
    void show() {
        this.fileChooser.setDialogTitle("Save New Sequence");
        this.fileChooser.setApproveButtonText("Choose");
        this.dialog.setLocationRelativeTo(this.dialog.getParent());
        this.dialog.setVisible(true);
    }


//~~PRIVATE OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initializeLabels() {
        for (JLabel l : this.labels) {
            this.dialog.getContentPane().add(l);
        }
        this.layout.putConstraint(
            SpringLayout.NORTH, this.labels.get(0),
            PADDING_VERTICAL,
            SpringLayout.NORTH, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.labels.get(0),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.textFields.get(0)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.labels.get(1),
            PADDING_VERTICAL,
            SpringLayout.SOUTH, this.textFields.get(0)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.labels.get(1),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.textFields.get(1)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.labels.get(2),
            PADDING_VERTICAL,
            SpringLayout.SOUTH, this.textFields.get(0)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.labels.get(2),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.textFields.get(2)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.labels.get(3),
            PADDING_VERTICAL,
            SpringLayout.SOUTH, this.textFields.get(0)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.labels.get(3),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.textFields.get(3)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.labels.get(4),
            PADDING_VERTICAL,
            SpringLayout.SOUTH, this.textFields.get(1)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.labels.get(4),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.textFields.get(4)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.labels.get(5),
            PADDING_VERTICAL,
            SpringLayout.SOUTH, this.textFields.get(1)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.labels.get(5),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.textFields.get(5)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.labels.get(6),
            PADDING_VERTICAL,
            SpringLayout.SOUTH, this.textFields.get(4)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.labels.get(6),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.textFields.get(6)
        );
    }

    private void initializeTextFields() {
        for (JTextField f : this.textFields) {
            this.dialog.getContentPane().add(f);
        }
        this.layout.putConstraint(
            SpringLayout.NORTH, this.textFields.get(0),
            0,
            SpringLayout.SOUTH, this.labels.get(0)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.textFields.get(0),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.textFields.get(1),
            0,
            SpringLayout.SOUTH, this.labels.get(1)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.textFields.get(1),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.textFields.get(2),
            0,
            SpringLayout.SOUTH, this.labels.get(2)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.textFields.get(2),
            PADDING_HORIZONTAL,
            SpringLayout.EAST, this.textFields.get(1)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.textFields.get(3),
            0,
            SpringLayout.SOUTH, this.labels.get(3)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.textFields.get(3),
            PADDING_HORIZONTAL,
            SpringLayout.EAST, this.textFields.get(2)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.textFields.get(4),
            0,
            SpringLayout.SOUTH, this.labels.get(4)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.textFields.get(4),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.textFields.get(5),
            0,
            SpringLayout.SOUTH, this.labels.get(5)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.textFields.get(5),
            PADDING_HORIZONTAL,
            SpringLayout.EAST, this.textFields.get(4)
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.textFields.get(6),
            0,
            SpringLayout.SOUTH, this.labels.get(6)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.textFields.get(6),
            PADDING_HORIZONTAL,
            SpringLayout.WEST, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.EAST, this.textFields.get(6),
            -PADDING_HORIZONTAL,
            SpringLayout.WEST, this.buttons.get(0)
        );
    }

    private void initializeButtons() {
        for (JButton b : this.buttons) {
            this.dialog.getContentPane().add(b);
        }
        this.layout.putConstraint(
            SpringLayout.NORTH, this.buttons.get(0),
            0,
            SpringLayout.NORTH, this.textFields.get(6)
        );
        this.layout.putConstraint(
            SpringLayout.SOUTH, this.buttons.get(0),
            0,
            SpringLayout.SOUTH, this.textFields.get(6)
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.buttons.get(0),
            -24,
            SpringLayout.EAST, this.buttons.get(0)
        );
        this.layout.putConstraint(
            SpringLayout.EAST, this.buttons.get(0),
            -PADDING_HORIZONTAL,
            SpringLayout.EAST, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.buttons.get(1),
            -BUTTON_HEIGHT,
            SpringLayout.SOUTH, this.buttons.get(1)
        );
        this.layout.putConstraint(
            SpringLayout.SOUTH, this.buttons.get(1),
            -PADDING_VERTICAL,
            SpringLayout.SOUTH, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.buttons.get(1),
            -BUTTON_WIDTH * 2,
            SpringLayout.EAST, this.buttons.get(1)
        );
        this.layout.putConstraint(
            SpringLayout.EAST, this.buttons.get(1),
            -PADDING_HORIZONTAL,
            SpringLayout.EAST, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.NORTH, this.buttons.get(2),
            -BUTTON_HEIGHT,
            SpringLayout.SOUTH, this.buttons.get(2)
        );
        this.layout.putConstraint(
            SpringLayout.SOUTH, this.buttons.get(2),
            -PADDING_VERTICAL,
            SpringLayout.SOUTH, this.dialog.getContentPane()
        );
        this.layout.putConstraint(
            SpringLayout.WEST, this.buttons.get(2),
            -BUTTON_WIDTH * 2,
            SpringLayout.EAST, this.buttons.get(2)
        );
        this.layout.putConstraint(
            SpringLayout.EAST, this.buttons.get(2),
            -PADDING_HORIZONTAL,
            SpringLayout.WEST, this.buttons.get(1)
        );
    }


//~~~~CONSTRUCTOR HELPERS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    List<JLabel> makeLabels() {
        List<JLabel> out = new ArrayList<>(7);
        out.add(new JLabel("NAME"));
        out.add(new JLabel("START"));
        out.add(new JLabel("END"));
        out.add(new JLabel("RATE"));
        out.add(new JLabel("WIDTH"));
        out.add(new JLabel("HEIGHT"));
        out.add(new JLabel("PATH"));

        for (JLabel l : out) {
            l.setFont(FONT_DIALOG);
        }
        return Collections.unmodifiableList(out);
    }

    private List<JTextField> makeTextFields() {
        List<JTextField> out = new ArrayList<>(7);
        out.add(new JTextField(20));
        out.add(new JTextField(10));
        out.add(new JTextField(10));
        out.add(new JTextField(6 ));
        out.add(new JTextField(8 ));
        out.add(new JTextField(8 ));
        out.add(new JTextField(20));

        for (byte b = 0; b < out.size(); b++) {
            if (b != 0 && b != out.size() - 1) {
                out.get(b).setHorizontalAlignment(SwingConstants.RIGHT);
            }
            out.get(b).setFont(FONT_TEXT_FIELD);
        }
        return Collections.unmodifiableList(out);
    }

    private List<JButton> makeButtons() {
        List<JButton> out = new ArrayList<>(3);
        out.add(new InterfaceButton("^"     , "browse", this, "Browse..."));
        out.add(new InterfaceButton("CANCEL", "cancel", this));
        out.add(new InterfaceButton("MAKE"  , "make"  , this));
        return Collections.unmodifiableList(out);
    }
}
