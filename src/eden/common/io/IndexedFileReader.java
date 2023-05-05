package eden.common.io;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

/**
 * A {@code IndexedFileReader} reads data from files that are formatted in an
 * index-value manner.
 *
 * @author Brendon
 * @version u0r5, 11/05/2018.
 */
public interface IndexedFileReader extends FileWorker {
  /**
   * Reads data from a file at the given entry index
   *
   * @param index Index number defining the location of the data
   *
   * @return The read data
   *
   * @throws IllegalArgumentException If {@code index < 0}
   *
   * @throws IOException If a read operation fails or is interrupted
   *
   * @throws IndexOutOfBoundsException If the end of file is reached
   * unexpectedly before the given index
   *
   * @throws EOFException If the end of file is reached unexpectedly while
   * reading a value
   */
  String read(int index)
    throws IllegalArgumentException, IOException, IndexOutOfBoundsException;
  /**
   * Reads all data from a file to a {@code List}
   *
   * @return A {@code List} of read data
   *
   * @throws IOException If a read operation fails or is interrupted
   *
   * @throws EOFException If the end of file is reached unexpectedly while
   * reading a value
   */
  List<String> readToList() throws IOException;
}
