// TODO: JUnit conversion to standard assert statements
////package eden.common.audio;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.UnsupportedAudioFileException;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//// for devB
//public class OutputSourceTester {
//
//    public static final File FILE = new File("test/MTN_SNC.WAV");
//    public static final byte[] BUFFER = new byte[4800];
//    public static final int STREAM_SIZE = (44100 * 4 * 16 * 2) / 8;
//    //                                     rate    len bit  ch   byte
//
//    public AudioFormat format;
//    public BufferedInputStream stream;
//    public OutputSource obj;
//
//
//    @BeforeEach
//    public void initialize() throws UnsupportedAudioFileException, IOException {
//        format = AudioSystem.getAudioFileFormat(FILE).getFormat();
//
//        stream = new BufferedInputStream(
//            AudioSystem.getAudioInputStream(FILE)
//        );
//        obj = new OutputSource(stream, format);
//        Arrays.fill(BUFFER, (byte) 0);
//    }
//
//
//    @Test
//    public void testStreamSize() {
//        assertTrue(obj.getStreamSize() == STREAM_SIZE);
//    }
//
//    @Test
//    public void testRead() {
//        assertTrue(obj.read(BUFFER) == BUFFER.length);
//        assertFalse(obj.isLoop());
//        assertFalse(obj.isDone());
//        assertFalse(obj.isDead());
//        assertTrue(BUFFER[0] != 0);
//        assertTrue(BUFFER[2] == 0);
//        assertTrue(BUFFER[BUFFER.length - 1] == 0);
//        assertTrue(BUFFER[BUFFER.length - 3] != 0);
//    }
//
//    @Test
//    public void testSkipChannelSwap() {
//        obj.skip(2);
//        obj.read(BUFFER);
//        assertTrue(BUFFER[0] == 0);
//        assertTrue(BUFFER[2] != 0);
//        assertTrue(BUFFER[BUFFER.length - 1] != 0);
//        assertTrue(BUFFER[BUFFER.length - 3] == 0);
//        obj.skip(2);
//        obj.read(BUFFER);
//        assertTrue(BUFFER[0] != 0);
//        assertTrue(BUFFER[2] == 0);
//        assertTrue(BUFFER[BUFFER.length - 1] == 0);
//        assertTrue(BUFFER[BUFFER.length - 3] != 0);
//    }
//
//    @Test
//    public void testSkipToEnd() {
//        obj.skip(obj.getStreamSize());
//        assertTrue(obj.isDone());
//    }
//
//    @Test
//    public void testMark() {
//        obj.skip(1234);
//        obj.mark();
//        assertTrue(obj.getMarkPosition() == 1234);
//    }
//
//    @Test
//    public void testJumpToMark() {
//        obj.skip(1234);
//        obj.mark();
//        obj.skip(4321);
//        obj.jumpToMark();
//        assertTrue(obj.getAvailable() == obj.getStreamSize() - 1234);
//    }
//
//    @Test
//    public void testJumpToStart() {
//        obj.skip(1234);
//        obj.jumpToStart();
//        assertTrue(obj.getAvailable() == obj.getStreamSize());
//    }
//
//    @Test
//    public void testLoop() {
//        obj.setLoop(true);
//        assertTrue(obj.isLoop());
//        obj.skip(obj.getStreamSize());
//        assertFalse(obj.isDone());
//        obj.read(BUFFER);
//        assertFalse(obj.isDone());
//        assertTrue(BUFFER[0] != 0);
//        assertTrue(BUFFER[2] == 0);
//        assertTrue(BUFFER[BUFFER.length - 1] == 0);
//        assertTrue(BUFFER[BUFFER.length - 3] != 0);
//    }
//
//    @Test
//    public void testDone() {
//        obj.skip(obj.getStreamSize());
//        assertTrue(obj.isDone());
//    }
//
//    @Test
//    public void testDead() {
//        obj.close();
//        assertTrue(obj.isDead());
//        assertTrue(obj.getDeathCause() instanceof IOException);
//        assertTrue(obj.read(BUFFER) == -1);
//    }
//
//    @Test
//    public void testBadArgsRead() {
//        assertTrue(obj.read(null) == 0);
//    }
//
//    @Test
//    public void testBadArgsSkip() {
//        obj.skip(-1);
//        assertTrue(obj.getAvailable() == obj.getStreamSize());
//    }
//}
