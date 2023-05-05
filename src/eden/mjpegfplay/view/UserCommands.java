package eden.mjpegfplay.view;

/**
 * This class provides definitions to user commands. It is intended to be shared
 * among other classes within its package to maintain consistency across their
 * implementations.
 *
 * User commands whose String representations end with an underscore (_) require
 * additional parameters.
 *
 * @author Brendon
 * @version u0r5, 05/05/2023.
 */
class UserCommands {

  static final String F_NEW = "F_n";
  static final String F_LOAD = "F_l";
  static final String F_LOAD_FREEZING = "F_lFreezing";
  static final String F_LOAD_MUSIC = "F_lMusic";
  static final String F_SAVE = "F_s";
  static final String F_RELOAD = "F_r";
  static final String F_EJECT = "F_e";
  static final String F_QUIT = "F_q";
  static final String V_MULTI_WINDOW = "V_wMulti";
  static final String V_SINGLE_WINDOW = "V_wSingle";
  static final String V_SEQUENCE_INFORMATION = "V_seqInfo";
  static final String V_RENDER_STATISTICS = "V_renderStats";
  static final String T_PLAY = "T_pl";
  static final String T_PAUSE = "T_pa";
  static final String T_STOP = "T_st";
  static final String T_FAST_REWIND = "T_fR";
  static final String T_FAST_FORWARD = "T_fF";
  static final String T_STEP_BACKWARD = "T_sB";
  static final String T_STEP_FORWARD = "T_sF";
  static final String T_JUMP_TO_START = "T_jS";
  static final String T_JUMP_TO_END = "T_jE";
  static final String T_JUMP_TO_FRAME = "T_jF";
  static final String T_TRICKPLAY = "T_tp";
  static final String T_SET_LENSCOUNT = "T_sL";
  static final String A_MUTE = "A_mT";
  static final String A_UNMUTE = "A_mF";
  static final String A_TOGGLE_MUTE = "A_m";
  static final String A_VOLUME = "A_v_";
  static final String A_TRACK = "A_t_";
  static final String H_ABOUT = "H_a";

  /** To prevent instantiations of this class */
  private UserCommands() {}
}
