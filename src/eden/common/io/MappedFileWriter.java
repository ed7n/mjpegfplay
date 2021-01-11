// @formatter:off
package eden.common.io;

import java.io.IOException;
import java.util.Map;


/**
 *  A {@code MappedFileWriter} writes data from {@code Maps} to a file
 *  formatted in a key-value manner.
 *
 *  @author     Brendon
 *  @version    u0r5, 11/05/2018.
 */
public interface MappedFileWriter extends FileWorker {

    /**
     *  Writes data from the given {@code Map} to a file
     *
     *  @param      map
     *              {@code Map} from which data is to be read
     *
     *  @throws     IllegalArgumentException
     *              If {@code map == null}
     *
     *  @throws     IOException
     *              If a write operation fails or is interrupted
     */
    void write(Map<String, String> map) throws
        IllegalArgumentException,
        IOException;
}
