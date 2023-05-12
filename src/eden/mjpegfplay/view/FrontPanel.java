package eden.mjpegfplay.view;

import static eden.common.shared.Constants.NUL_STRING;
import static eden.mjpegfplay.model.TransportConstants.*;
import static eden.mjpegfplay.view.FrontPanelConstants.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JComponent;

/**
 * @author Brendon
 * @version u0r4, 11/06/2021.
 *
 * @see FrontPanelInterface
 */
class FrontPanel extends JComponent implements FrontPanelInterface {

  /** StringBuilder with which display Strings are to be made */
  private final StringBuilder stringBuilder = new StringBuilder(TEXT_LENGTH);
  /** Number of pending paint calls */
  private final AtomicInteger calls = new AtomicInteger(0);
  /** Top row text */
  private String text0 = NUL_STRING;
  /** Bottom row text */
  private String text1 = NUL_STRING;
  /** WAIT indicator pin */
  private Boolean wait = false;
  /** GUI indicator pin */
  private Boolean gui = false;
  /** PAUSE indicator pin */
  private Boolean pause = false;
  /** SCAN indicator pin */
  private Boolean scan = false;
  /** OSD indicator pin */
  private Boolean osd = false;
  /** FLOAT indicator pin */
  private Boolean floating = false;
  /** LOOP indicator pin */
  private Boolean loop = false;
  /** HS indicator pin */
  private Boolean highSpeed = false;
  /** REV indicator pin */
  private Boolean mute = false;

  /** Makes a FrontPanel */
  FrontPanel() {
    setName("Front Panel");
    setMaximumSize(SIZE);
    setMinimumSize(SIZE);
    setPreferredSize(SIZE);
    setSize(SIZE);
  }

  /** Paints this {@code FrontPanel} */
  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    drawDecorations(g2);
    drawTexts(g2);
    g2.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    );
    drawIndicators(g2);
  }

  /** Runs this {@code FrontPanel} */
  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      synchronized (this) {
        try {
          wait();
        } catch (InterruptedException exception) {
          return;
        }
      }
      do {
        repaint();
      } while (this.calls.decrementAndGet() > 0);
    }
  }

  /** {@inheritDoc} */
  @Override
  public synchronized void call() {
    if (this.calls.compareAndSet(MAXIMUM_CALLS, MAXIMUM_CALLS)) {
      return;
    }
    this.calls.incrementAndGet();
    notifyAll();
  }

  /** {@inheritDoc} */
  @Override
  public void paint() {
    paintComponent(getGraphics());
  }

  /** {@inheritDoc} */
  @Override
  public String getText0() {
    return this.text0;
  }

  /** {@inheritDoc} */
  @Override
  public String getText1() {
    return this.text1;
  }

  /** {@inheritDoc} */
  @Override
  public void setText0(String text0) {
    if (text0 != null) {
      this.text0 = text0;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void setText1(String text1) {
    if (text1 != null) {
      this.text1 = text1;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void setWait(boolean wait) {
    this.wait = wait;
  }

  /** {@inheritDoc} */
  @Override
  public void setGui(boolean gui) {
    this.gui = gui;
  }

  /** {@inheritDoc} */
  @Override
  public void setPause(boolean pause) {
    this.pause = pause;
  }

  /** {@inheritDoc} */
  @Override
  public void setScan(boolean scan) {
    this.scan = scan;
  }

  /** {@inheritDoc} */
  @Override
  public void setOsd(boolean osd) {
    this.osd = osd;
  }

  /** {@inheritDoc} */
  @Override
  public void setFloating(boolean floating) {
    this.floating = floating;
  }

  /** {@inheritDoc} */
  @Override
  public void setLoop(boolean loop) {
    this.loop = loop;
  }

  /** {@inheritDoc} */
  @Override
  public void setHighSpeed(boolean highSpeed) {
    this.highSpeed = highSpeed;
  }

  /** {@inheritDoc} */
  @Override
  public void setMute(boolean mute) {
    this.mute = mute;
  }

  /** {@inheritDoc} */
  @Override
  public void setTexts(String text0, String text1) {
    setText0(text0);
    setText1(text1);
  }

  /** {@inheritDoc} */
  @Override
  public void clearTexts() {
    this.text0 = NUL_STRING;
    this.text1 = NUL_STRING;
  }

  /** {@inheritDoc} */
  @Override
  public void setIndicators(
    int wait,
    int gui,
    int pause,
    int scan,
    int osd,
    int floating,
    int loop,
    int highSpeed,
    int reverse
  ) {
    setPin(this.wait, wait);
    setPin(this.gui, gui);
    setPin(this.pause, pause);
    setPin(this.scan, scan);
    setPin(this.osd, osd);
    setPin(this.floating, floating);
    setPin(this.loop, loop);
    setPin(this.highSpeed, highSpeed);
    setPin(this.mute, reverse);
  }

  /** {@inheritDoc} */
  @Override
  public void clearIndicators() {
    this.wait = false;
    this.gui = false;
    this.pause = false;
    this.scan = false;
    this.osd = false;
    this.floating = false;
    this.loop = false;
    this.highSpeed = false;
    this.mute = false;
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    clearTexts();
    clearIndicators();
  }

  /** {@inheritDoc} */
  @Override
  public void setFromMode(int mode) {
    switch (mode) {
      case CLOSE:
        this.osd = false;
        this.loop = false;
        this.highSpeed = false;
      case PLAY:
      case IDLE:
        this.pause = false;
        this.scan = false;
        break;
      case PAUSE:
        this.pause = true;
        this.scan = false;
        break;
      case FAST_FORWARD:
      case FAST_REWIND:
      case TRICKPLAY:
        this.pause = false;
        this.scan = true;
        break;
      case MUTE:
        this.mute = true;
        break;
      case UNMUTE:
        this.mute = false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void setToTest() {
    setTexts("0123456789012345", "6543210987654321");
    setIndicators(1, 1, 1, 1, 1, 1, 1, 1, 1);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isWait() {
    return this.wait;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isGui() {
    return this.gui;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPause() {
    return this.pause;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isScan() {
    return this.scan;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOsd() {
    return this.osd;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFloating() {
    return this.floating;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isLoop() {
    return this.loop;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMute() {
    return this.mute;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isHighSpeed() {
    return this.highSpeed;
  }

  private void setPin(Boolean pin, int input) {
    if (input == 1) {
      pin = true;
    } else if (input == 0) {
      pin = false;
    }
  }

  private void drawDecorations(Graphics2D g) {
    g.setColor(COLOR_BACKGROUND);
    g.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
    g.setColor(COLOR_BORDER);
    g.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 4, 4);
    g.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 4, 4);
  }

  private void drawTexts(Graphics2D g) {
    g.setFont(FONT_TEXT);
    g.drawString(formatToPanel(this.text0, false), 18, Y_TEXT - 23);
    g.drawString(formatToPanel(this.text1, false), 18, Y_TEXT);
  }

  private void drawIndicators(Graphics2D g) {
    g.setFont(this.wait ? FONT_INDICATOR_ALERT : FONT_INDICATOR_ALERT_OFF);
    g.drawString("  WAIT  ", 18, Y_INDICATOR_TOP);
    g.setFont(this.gui ? FONT_INDICATOR : FONT_INDICATOR_OFF);
    g.drawString("  GUI  ", 63, Y_INDICATOR_TOP);
    g.setFont(this.pause ? FONT_INDICATOR_ALERT : FONT_INDICATOR_ALERT_OFF);
    g.drawString("  PAUSE  ", 101, Y_INDICATOR_TOP);
    g.setFont(this.scan ? FONT_INDICATOR_SCAN : FONT_INDICATOR_SCAN_OFF);
    g.drawString("  SCAN  ", 156, Y_INDICATOR_TOP);
    g.setFont(this.osd ? FONT_INDICATOR : FONT_INDICATOR_OFF);
    g.drawString("  OSD  ", 204, Y_INDICATOR_TOP);
    g.setFont(this.floating ? FONT_INDICATOR : FONT_INDICATOR_OFF);
    g.drawString("  FLOAT  ", 18, Y_INDICATOR_BOTTOM);
    g.setFont(this.loop ? FONT_INDICATOR_SCAN : FONT_INDICATOR_SCAN_OFF);
    g.drawString("  LOOP  ", 71, Y_INDICATOR_BOTTOM);
    g.setFont(this.mute ? FONT_INDICATOR_ALERT : FONT_INDICATOR_ALERT_OFF);
    g.drawString("  MUTE  ", 120, Y_INDICATOR_BOTTOM);
    g.setFont(this.highSpeed ? FONT_INDICATOR : FONT_INDICATOR_OFF);
    g.drawString("  HS  ", 167, Y_INDICATOR_BOTTOM);
  }

  private String formatToPanel(String string, boolean leanRight) {
    string = string.toUpperCase();
    this.stringBuilder.setLength(0);
    if (string.length() > TEXT_LENGTH) {
      return this.stringBuilder.append(string.substring(0, TEXT_LENGTH))
        .toString();
    }
    byte spacesLeft = leanRight
      ? (byte) Math.ceil((double) (TEXT_LENGTH - string.length()) / 2)
      : (byte) Math.floor((double) (TEXT_LENGTH - string.length()) / 2);
    byte spacesRght = (byte) (TEXT_LENGTH - string.length() - spacesLeft);
    while (spacesLeft-- > 0) {
      this.stringBuilder.append(' ');
    }
    this.stringBuilder.append(string);
    while (spacesRght-- > 0) {
      this.stringBuilder.append(' ');
    }
    return this.stringBuilder.toString();
  }
}
