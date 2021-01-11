// TODO: JUnit conversion to standard assert statements

//// @formatter:off
//package eden.common.io;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.NoSuchElementException;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//// for u0r5
//public class ConfigFileIOTester {
//
//    public static final String FILE = "test/TEST_CFG";
//
//    public  MappedFileReader readerMapped;
//    public  MappedFileWriter writerMapped;
//    public IndexedFileReader readerIndexed;
//    public IndexedFileWriter writerIndexed;
//
//
//    @BeforeEach
//    public void initialize() {
//        readerMapped  = new ConfigFileReader(FILE);
//        writerMapped  = new ConfigFileWriter(FILE);
//        readerIndexed = new ConfigFileReader(FILE);
//        writerIndexed = new ConfigFileWriter(FILE);
//    }
//
//
//    @Test
//    public void checkFile() {
//        assertTrue(new File(FILE).exists());
//    }
//
//    @Test
//    public void testValues() throws IOException {
//        assertTrue( readerMapped.read("key"   ).equals("value"));
//        assertFalse(readerMapped.read("number").equals("09232018"));
//        assertTrue( readerMapped.read("word"  ).equals("ConfigFileReader"));
//        assertFalse(readerMapped.read("phrase").equals("was born"));
//        assertTrue( readerMapped.read("bell"  ).equals("\u0007"));
//
//        assertTrue(readerMapped.read("LECTURE").equals(
//            "A configuration entry data may span multiple lines." +
//            " Two lines are concatenated as is; no whitespace between them." +
//            " This line should not :LECTURE close this entry."
//        ));
//    }
//
//    @Test
//    public void testKeys() throws IOException {
//        assertTrue(readerMapped.read("TEST_KEY").equals("OKC"));
//        assertTrue(readerMapped.read("_TESTKEY").equals("OKL"));
//        assertTrue(readerMapped.read("TESTKEY_").equals("OKR"));
//    }
//
//    @Test
//    public void testBadKey() throws IOException {
//
//        try {
//            assertTrue(readerMapped.read("TEST+KEY").equals("Don't touch me!"));
//        } catch (NoSuchElementException e) {}
//    }
//
//    @Test
//    public void testEmptyKey() throws IOException {
//
//        try {
//            assertTrue(readerMapped.read("").equals(
//                "Don't touch me either!"));
//        } catch (NoSuchElementException e) {}
//    }
//
//    @Test
//    public void testToMap() throws IOException {
//        Map<String, String> map = readerMapped.readToMap();
//
//        assertTrue( map.get("key"   ).equals("value"));
//        assertFalse(map.get("number").equals("09232018"));
//        assertTrue( map.get("word"  ).equals("ConfigFileReader"));
//        assertFalse(map.get("phrase").equals("was born"));
//        assertTrue( map.get("bell"  ).equals("\u0007"));
//
//        assertTrue(map.get("LECTURE").equals(
//            "A configuration entry data may span multiple lines." +
//            " Two lines are concatenated as is; no whitespace between them." +
//            " This line should not :LECTURE close this entry."
//        ));
//    }
//
//    @Test
//    public void testIndexes() throws IOException {
//        assertTrue( readerIndexed.read(0).equals("value"));
//        assertFalse(readerIndexed.read(1).equals("09232018"));
//        assertTrue( readerIndexed.read(2).equals("ConfigFileReader"));
//        assertFalse(readerIndexed.read(3).equals("was born"));
//        assertTrue( readerIndexed.read(4).equals("\u0007"));
//
//        assertTrue(readerIndexed.read(10).equals("Touch me by index only."));
//
//        assertTrue(readerIndexed.read(11).equals(
//            "A configuration entry data may span multiple lines." +
//            " Two lines are concatenated as is; no whitespace between them." +
//            " This line should not :LECTURE close this entry."
//        ));
//    }
//
//    @Test
//    public void testBadIndex() throws IOException {
//
//        try {
//            readerIndexed.read(13);
//            assertTrue(false);
//        } catch (IndexOutOfBoundsException e) {}
//    }
//
//    @Test
//    public void testToList() throws IOException {
//        List<String> list = readerIndexed.readToList();
//
//        assertTrue( list.get(0).equals("value"));
//        assertFalse(list.get(1).equals("09232018"));
//        assertTrue( list.get(2).equals("ConfigFileReader"));
//        assertFalse(list.get(3).equals("was born"));
//        assertTrue( list.get(4).equals("\u0007"));
//
//        assertTrue(list.get(10).equals("Touch me by index only."));
//
//        assertTrue(list.get(11).equals(
//            "A configuration entry data may span multiple lines." +
//            " Two lines are concatenated as is; no whitespace between them." +
//            " This line should not :LECTURE close this entry."
//        ));
//    }
//
//    @Test
//    public void testFromMap() throws IOException {
//        ConfigFileReader reader = new ConfigFileReader("TEST_CFG_MAP");
//        ConfigFileWriter writer = new ConfigFileWriter("TEST_CFG_MAP");
//        Map<String, String> map = readerMapped.readToMap();
//
//        writer.write(map);
//        assertTrue(map.equals(reader.readToMap()));
//    }
//
//    @Test
//    public void testFromList() throws IOException {
//        ConfigFileReader reader = new ConfigFileReader("TEST_CFG_LIST");
//        ConfigFileWriter writer = new ConfigFileWriter("TEST_CFG_LIST");
//        List<String> list = readerIndexed.readToList();
//
//        writer.write(list);
//        assertTrue(list.equals(reader.readToList()));
//    }
//
//    @Test
//    public void testBadArgsNullKey() throws IOException {
//
//        try {
//            readerMapped.read(null);
//            assertTrue(false);
//        } catch (IllegalArgumentException e){}
//    }
//
//    @Test
//    public void testBadArgsNullMap() throws IOException {
//
//        try {
//            writerMapped.write(null);
//            assertTrue(false);
//        } catch (IllegalArgumentException e){}
//    }
//
//    @Test
//    public void testBadArgsIndexUnder() throws IOException {
//
//        try {
//            readerIndexed.read(-1);
//            assertTrue(false);
//        } catch (IllegalArgumentException e){}
//    }
//
//    @Test
//    public void testBadArgsNullList() throws IOException {
//
//        try {
//            writerIndexed.write(null);
//            assertTrue(false);
//        } catch (IllegalArgumentException e){}
//    }
//}
