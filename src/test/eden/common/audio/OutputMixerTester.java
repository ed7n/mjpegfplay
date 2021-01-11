// TODO: JUnit conversion to standard assert statements

//// @formatter:off
//package eden.common.audio;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.util.Random;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.UnsupportedAudioFileException;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//// for devB
//public class OutputMixerTester {
//
//    public static final Random RANDOM = new Random(System.currentTimeMillis());
//    public static final File   FILE_0 = new File("test/MTN_SNC.WAV");
//    public static final File   FILE_1 = new File("test/MTN_SNCI.WAV");
//
//    public AudioFormat format;
//    public BufferedInputStream stream0;
//    public BufferedInputStream stream1;
//    public OutputSource source0;
//    public OutputSource source1;
//    public OutputMixer obj;
//
//
//    @BeforeEach
//    public void initialize() throws UnsupportedAudioFileException, IOException {
//        format = AudioSystem.getAudioFileFormat(FILE_0).getFormat();
//
//        stream0 = new BufferedInputStream(
//            AudioSystem.getAudioInputStream(FILE_0)
//        );
//        stream1 = new BufferedInputStream(
//            AudioSystem.getAudioInputStream(FILE_1)
//        );
//        source0 = new OutputSource(stream0, format);
//        source1 = new OutputSource(stream1, format);
//        obj = new OutputMixer((byte) 4);
//    }
//
//
//    @Test
//    public void testGetFreeChannel() {
//        assertTrue(obj.getFreeChannel() == 0);
//        assertTrue(obj.getFreeChannel() == 1);
//        assertTrue(obj.getFreeChannel() == 2);
//        assertTrue(obj.getFreeChannel() == 3);
//        assertTrue(obj.getFreeChannel() == 0);
//    }
//
//    @Test
//    public void testAttachAuto() throws ChannelNotFreeException {
//        obj.attach(source0, 1);
//        obj.attach(source1, 2);
//        assertTrue(obj.attach(source1) == 0);
//        assertTrue(obj.attach(source0) == 3);
//    }
//
//    @Test
//    public void testAttachManual() throws ChannelNotFreeException {
//        obj.attach(source0, 1);
//        obj.attach(source1, 2);
//        assertFalse(obj.getChannel(0) == source0);
//        assertFalse(obj.getChannel(1) == source1);
//        assertFalse(obj.getChannel(2) == null);
//        assertTrue( obj.getChannel(0) == null);
//        assertTrue( obj.getChannel(1) == source0);
//        assertTrue( obj.getChannel(2) == source1);
//        assertTrue( obj.getChannel(3) == null);
//    }
//
//    @Test
//    public void testAttachReplace() {
//        int i = makeRandomInt();
//        obj.attachAndReplace(source0, i);
//        assertTrue(obj.getChannel(i) == source0);
//        obj.attachAndReplace(source1, i);
//        assertTrue(obj.getChannel(i) == source1);
//    }
//
//    @Test
//    public void testAttachFull() {
//        for (int i = 0; i < obj.getCapacity(); i++) {
//            obj.attach(source0);
//        }
//        assertTrue(obj.attach(source1) < 0);
//    }
//
//    @Test
//    public void testDetach() throws ChannelNotFreeException {
//        int i = makeRandomInt();
//        obj.attach(source0, i);
//        assertTrue(obj.detach(i) == source0);
//        assertTrue(obj.detach(i) == null);
//    }
//
//    @Test
//    public void testBadArgsAttach() throws ChannelNotFreeException {
//        try {
//            obj.attach(source0, -1);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//
//        try {
//            obj.attach(source0, 4);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//    }
//
//    @Test
//    public void testBadArgsAttachReplace() {
//        try {
//            assertTrue(obj.attachAndReplace(source0, -1) == null);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//
//        try {
//            assertTrue(obj.attachAndReplace(source0, 4) == null);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//    }
//
//    @Test
//    public void testBadArgsDetach() {
//        try {
//            assertTrue(obj.detach(-1) == null);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//
//        try {
//            assertTrue(obj.detach(4) == null);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//    }
//
//    @Test
//    public void testBadArgsGetChannel() {
//        try {
//            assertTrue(obj.getChannel(-1) == null);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//
//        try {
//            assertTrue(obj.getChannel(4) == null);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//    }
//
//    @Test
//    public void testBadArgsGetDspData() {
//        try {
//            obj.getDspData(-1);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//
//        try {
//            obj.getDspData(4);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//    }
//
//    @Test
//    public void testBadArgsIsChannelFree() {
//        try {
//            assertTrue(obj.isChannelFree(4));
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//
//        try {
//            assertTrue(obj.isChannelFree(4));
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//    }
//
//    private int makeRandomInt() {
//        return Math.abs(RANDOM.nextInt() % 4);
//    }
//}
