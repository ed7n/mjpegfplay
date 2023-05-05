package eden.common.io;

import java.io.EOFException;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A {@code MappedFileReader} reads data from files that are formatted in a
 * key-value manner.
 *
 * @author Brendon
 * @version u0r5, 11/05/2018.
 */
public interface MappedFileReader extends FileWorker {
  /**
   * Reads data from a file at a given key
   *
   * @param key Key defining the entry name of the data
   *
   * @return The read data
   *
   * @throws IllegalArgumentException If {@code key == null}
   *
   * @throws IOException If a read operation fails or is interrupted
   *
   * @throws NoSuchElementException If the given key can not be found
   *
   * @throws EOFException If the end of file is reached unexpectedly while
   * reading a value
   */
  String read(String key)
    throws IllegalArgumentException, IOException, NoSuchElementException;
  /**
   * Reads all data from a file to a {@code Map}
   *
   * @return A {@code Map} of read data
   *
   * @throws IOException If a read operation fails or is interrupted
   *
   * @throws EOFException If the end of file is reached unexpectedly while
   * reading a value
   */
  Map<String, String> readToMap() throws IOException;
}
