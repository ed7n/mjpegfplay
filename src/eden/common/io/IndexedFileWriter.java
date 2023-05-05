package eden.common.io;

import java.io.IOException;
import java.util.List;

/**
 * An {@code IndexedFileWriter} writes data from {@code Lists} to a file
 * formatted in an index-value manner.
 *
 * @author Brendon
 * @version u0r5, 11/05/2018.
 */
public interface IndexedFileWriter extends FileWorker {
  /**
   * Writes data from the given {@code List} to a file
   *
   * @param list {@code List} from which data is to be read
   *
   * @throws IllegalArgumentException If {@code list == null}
   *
   * @throws IOException If a write operation fails or is interrupted
   */
  void write(List<String> list) throws IllegalArgumentException, IOException;
}
