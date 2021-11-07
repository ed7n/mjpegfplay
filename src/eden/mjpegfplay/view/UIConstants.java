package eden.mjpegfplay.view;

import eden.common.video.CSSColor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * This class provides definitions to UI element properties. It is intended to
 * be shared among other classes within its package to maintain consistency
 * across their implementations.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
class UIConstants {

  /** Minimum application main Frame size in pixels */
  static final Dimension SIZE_FRAME = new Dimension(640, 640);

  /** Minimum interface size in pixels */
  static final Dimension SIZE_INTERFACE = new Dimension(640, 160);

  /** Preferred render size in pixels */
  static final Dimension SIZE_RENDER = new Dimension(640, 480);

  /** Application Menu background Color */
  static final Color COLOR_MENU = new Color(31, 31, 31);

  /** Interface Panel background Color */
  static final Color COLOR_INTERFACE = new Color(15, 15, 15);

  /** Application Menu Font */
  static final Font FONT_MENU;

  /** Dialog Font */
  static final Font FONT_DIALOG;

  /** Label Font */
  static final Font FONT_LABEL;

  /** Button Font */
  static final Font FONT_BUTTON;

  /** TextField Font */
  static final Font FONT_TEXT_FIELD;

  static {
    HashMap<TextAttribute, Object> map = new HashMap<>();
    map.put(TextAttribute.FAMILY, Font.SANS_SERIF);
    map.put(TextAttribute.FOREGROUND, CSSColor.LIGHT_GOLDENROD_YELLOW);
    map.put(TextAttribute.SIZE, 12);
    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
    map.put(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_EXTENDED);
    FONT_MENU = new Font(map);
    map.put(TextAttribute.SIZE, 10);
    FONT_DIALOG = new Font(map);
    map.put(TextAttribute.SIZE, 8);
    FONT_LABEL = new Font(map);
    map.clear();
    map.put(TextAttribute.FAMILY, Font.MONOSPACED);
    map.put(TextAttribute.FOREGROUND, CSSColor.BLACK);
    map.put(TextAttribute.SIZE, 14);
    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    FONT_BUTTON = new Font(map);
    map.put(TextAttribute.FAMILY, Font.SANS_SERIF);
    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
    FONT_TEXT_FIELD = new Font(map);
  }

  /** Button Border */
  static final Border BUTTON_BORDER = LineBorder.createBlackLineBorder();

  /** Interface Panel horizontal padding in pixels */
  static final int PADDING_HORIZONTAL = 8;

  /** Interface Panel vertical padding in pixels */
  static final int PADDING_VERTICAL = 8;

  /** Button width in pixels */
  static final int BUTTON_WIDTH = 48;

  /** Button height in pixels */
  static final int BUTTON_HEIGHT = 32;

  /** Slider width in pixels */
  static final int SLIDER_WIDTH = BUTTON_WIDTH * 3;

  /** Spinner width in pixels */
  static final int SPINNER_WIDTH = BUTTON_WIDTH;

  /** Status display panel width in pixels */
  static final int PANEL_WIDTH = 256;

  /** Status display panel height in pixels */
  static final int PANEL_HEIGHT = 84;
}
