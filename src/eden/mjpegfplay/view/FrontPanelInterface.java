package eden.mjpegfplay.view;

import eden.mjpegfplay.model.TransportConstants;

/**
 * A {@code FrontPanelInterface} hides the {@code JComponent} definition from a
 * {@code FrontPanel} to simplify its interface to presenters.
 * <p>
 * A {@code FrontPanel} represents the states of this application on a {@code
 * JComponent} in a manner that mimics consumer A/V components in the 1990s.
 * <p>
 * It is designed to behave like a character LCD panel, on which the data pins
 * have to be set prior to updating the panel. On a {@code FrontPanel}, data
 * pins are loosely represented as its object fields, and can be set with their
 * respective mutators. Once finished, the call method can be used to signal
 * this {@code FrontPanel} to paint itself.
 * <p>
 * This behaviour is achieved with a {@code Thread} that waits for a call to
 * begin a paint cycle. It stacks multiple calls with an internal counter to
 * overcome ones that are missed while painting--that is, not waiting. However,
 * to prevent a backlog of unnecessary repaints, it caps at {@value
 * FrontPanelConstants#MAXIMUM_CALLS} calls.
 *
 * @author Brendon
 * @version u0r3, 11/28/2018.
 */
public interface FrontPanelInterface extends Runnable {

  /** Signals this {@code FrontPanel} to begin a paint cycle */
  void call();

  /** Paints this {@code FrontPanel} with the current {@code Thread} */
  void paint();

  /** Gets the top row text of this {@code FrontPanel} */
  String getText0();

  /** Gets the bottom row text of this {@code FrontPanel} */
  String getText1();

  /** Sets the top row text of this {@code FrontPanel} */
  void setText0(String text0);

  /** Sets the bottom row text of this {@code FrontPanel} */
  void setText1(String text1);

  /** Sets the WAIT indicator pin of this {@code FrontPanel} */
  void setWait(boolean wait);

  /** Sets the GUI indicator pin of this {@code FrontPanel} */
  void setGui(boolean gui);

  /** Sets the PAUSE indicator pin of this {@code FrontPanel} */
  void setPause(boolean pause);

  /** Sets the SCAN indicator pin of this {@code FrontPanel} */
  void setScan(boolean scan);

  /** Sets the OSD indicator pin of this {@code FrontPanel} */
  void setOsd(boolean osd);

  /** Sets the FLOAT indicator pin of this {@code FrontPanel} */
  void setFloating(boolean floating);

  /** Sets the LOOP indicator pin of this {@code FrontPanel} */
  void setLoop(boolean loop);

  /** Sets the HS indicator pin of this {@code FrontPanel} */
  void setHighSpeed(boolean highSpeed);

  /** Sets the MUTE indicator pin of this {@code FrontPanel} */
  void setMute(boolean mute);

  /**
   * Sets the texts of this {@code FrontPanel}. For each text represented as a
   * parameter, passing a {@code null} retains its value.
   */
  void setTexts(String text0, String text1);

  /** Clears the texts of this {@code FrontPanel} */
  void clearTexts();

  /**
   * Sets the indicator pins of this {@code FrontPanel}. For each indicator pin
   * represented as a parameter, passing {@code 0} sets it to low, {@code 1}
   * sets it to high, and others retain its value.
   */
  void setIndicators(int wait,
      int gui,
      int pause,
      int scan,
      int osd,
      int floating,
      int loop,
      int highSpeed,
      int reverse);

  /** Sets the indicator pins of this {@code FrontPanel} to low */
  void clearIndicators();

  /** Clears this {@code FrontPanel} */
  void clear();

  /**
   * Sets the indicator pins of this {@code FrontPanel} in accordance to the
   * given mode
   *
   * @see TransportConstants
   */
  void setFromMode(int mode);

  /**
   * Sets the texts and indicator pins of this {@code FrontPanel} to the test
   * pattern
   */
  void setToTest();

  /**
   * Returns whether the WAIT indicator pin of this {@code FrontPanel} is high
   */
  boolean isWait();

  /**
   * Returns whether the GUI indicator pin of this {@code FrontPanel} is high
   */
  boolean isGui();

  /**
   * Returns whether the PAUSE indicator pin of this {@code FrontPanel} is high
   */
  boolean isPause();

  /**
   * Returns whether the SCAN indicator pin of this {@code FrontPanel} is high
   */
  boolean isScan();

  /**
   * Returns whether the OSD indicator pin of this {@code FrontPanel} is high
   */
  boolean isOsd();

  /**
   * Returns whether the FLOAT indicator pin of this {@code FrontPanel} is high
   */
  boolean isFloating();

  /**
   * Returns whether the LOOP indicator pin of this {@code FrontPanel} is high
   */
  boolean isLoop();

  /**
   * Returns whether the MUTE indicator pin of this {@code FrontPanel} is high
   */
  boolean isMute();

  /**
   * Returns whether the HS indicator pin of this {@code FrontPanel} is high
   */
  boolean isHighSpeed();
}
