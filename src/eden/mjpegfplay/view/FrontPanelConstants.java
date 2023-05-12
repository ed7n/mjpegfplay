package eden.mjpegfplay.view;

import eden.common.video.CSSColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * This class provides definitions to UI element properties on a FrontPanel.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
public class FrontPanelConstants {

  /** Text row length in number of characters */
  public static final byte TEXT_LENGTH = 20;
  /** A {@code char[]} that can be used to align texts to the left */
  public static final char[] TEXT_BLANK = new char[TEXT_LENGTH];

  static {
    for (byte index = 0; index < TEXT_LENGTH; index++) {
      TEXT_BLANK[index] = ' ';
    }
  }

  /** JComponent Dimensions */
  static final Dimension SIZE = new Dimension(256, 96);
  /** Maximum number of pending paint calls */
  static final byte MAXIMUM_CALLS = 8;
  /** Background Color */
  static final Color COLOR_BACKGROUND = new Color(11, 3, 0);
  /** Border Color */
  static final Color COLOR_BORDER = CSSColor.BLACK;
  /** Text Font Color */
  static final Color COLOR_TEXT = new Color(191, 96, 0);
  /** Text background Color */
  static final Color COLOR_TEXT_BACKGROUND = new Color(15, 5, 0);
  /** Indicator Font Color */
  static final Color COLOR_INDICATOR = COLOR_TEXT;
  /** Disabled indicator Font Color */
  static final Color COLOR_INDICATOR_OFF = CSSColor.BLACK;
  /** Highlight indicator Font Color */
  static final Color COLOR_INDICATOR_HIGHLIGHT = CSSColor.BLACK;
  /** Alert indicator background Color */
  static final Color COLOR_INDICATOR_ALERT = new Color(191, 23, 0);
  /** Disabled alert indicator background Color */
  static final Color COLOR_INDICATOR_ALERT_OFF = new Color(23, 0, 0);
  /** Scan indicator background Color */
  static final Color COLOR_INDICATOR_SPECIAL = new Color(15, 160, 0);
  /** Disabled scan indicator background Color */
  static final Color COLOR_INDICATOR_SPECIAL_OFF = new Color(0, 15, 0);
  /** Text Font */
  static final Font FONT_TEXT;
  /** Indicator Font */
  static final Font FONT_INDICATOR;
  /** Disabled indicator Font */
  static final Font FONT_INDICATOR_OFF;
  /** Alert indicator Font */
  static final Font FONT_INDICATOR_ALERT;
  /** Disabled alert indicator Font */
  static final Font FONT_INDICATOR_ALERT_OFF;
  /** Scan indicator Font */
  static final Font FONT_INDICATOR_SCAN;
  /** Disabled scan indicator Font */
  static final Font FONT_INDICATOR_SCAN_OFF;

  /* Some crazy Map-based Font instantiations */
  static {
    HashMap<TextAttribute, Object> map = new HashMap<>();
    map.put(TextAttribute.BACKGROUND, COLOR_TEXT_BACKGROUND);
    map.put(TextAttribute.FAMILY, Font.MONOSPACED);
    map.put(TextAttribute.FOREGROUND, COLOR_TEXT);
    map.put(TextAttribute.SIZE, 18);
    FONT_TEXT = new Font(map);
    map.remove(TextAttribute.BACKGROUND);
    map.put(TextAttribute.FAMILY, Font.SANS_SERIF);
    map.put(TextAttribute.FOREGROUND, COLOR_INDICATOR);
    map.put(TextAttribute.SIZE, 8);
    map.put(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_EXTENDED);
    FONT_INDICATOR = new Font(map);
    map.put(TextAttribute.FOREGROUND, COLOR_INDICATOR_OFF);
    FONT_INDICATOR_OFF = new Font(map);
    map.put(TextAttribute.BACKGROUND, COLOR_INDICATOR_ALERT);
    map.put(TextAttribute.FOREGROUND, COLOR_INDICATOR_HIGHLIGHT);
    FONT_INDICATOR_ALERT = new Font(map);
    map.put(TextAttribute.BACKGROUND, COLOR_INDICATOR_ALERT_OFF);
    FONT_INDICATOR_ALERT_OFF = new Font(map);
    map.put(TextAttribute.BACKGROUND, COLOR_INDICATOR_SPECIAL);
    map.put(TextAttribute.FOREGROUND, COLOR_INDICATOR_HIGHLIGHT);
    FONT_INDICATOR_SCAN = new Font(map);
    map.put(TextAttribute.BACKGROUND, COLOR_INDICATOR_SPECIAL_OFF);
    FONT_INDICATOR_SCAN_OFF = new Font(map);
  }

  /** Text Y-coordinate in pixels */
  static final byte Y_TEXT = 61;
  /** Top indicator Y-coordinate in pixels */
  static final byte Y_INDICATOR_TOP = 14;
  /** Bottom indicator Y-coordinate in pixels */
  static final byte Y_INDICATOR_BOTTOM = 78;

  /** To prevent instantiations of this class */
  private FrontPanelConstants() {}
}
