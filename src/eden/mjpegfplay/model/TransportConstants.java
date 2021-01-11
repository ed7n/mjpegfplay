// @formatter:off
package eden.mjpegfplay.model;


/**
 *  This class provides definitions to {@code Sequence} playback transport
 *  states and commands. Unless specified, each state has its secondary
 *  definition that defines the number of points to advance per step.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
public class TransportConstants {

//~~PUBLIC CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Fast rewind mode */
    public static final byte FAST_REWIND = -4;

    /** Trickplay (reverse play) mode */
    public static final byte TRICKPLAY = -1;

    /** Pause mode */
    public static final byte PAUSE = 0;

    /** Play mode */
    public static final byte PLAY = 1;

    /** Fast forward mode */
    public static final byte FAST_FORWARD = 4;

    /** Idle mode. This does not have the secondary definition. */
    public static final byte IDLE = 17;

    /** Close operation. This does not have the secondary definition. */
    public static final byte CLOSE = 18;

    /** Mute operation. This does not have the secondary definition. */
    public static final byte MUTE = 19;

    /** Un-mute operation. This does not have the secondary definition. */
    public static final byte UNMUTE = 20;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** To prevent instantiations of this class */
    private TransportConstants(){}
}
