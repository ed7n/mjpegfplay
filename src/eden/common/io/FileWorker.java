package eden.common.io;

/**
 * A {@code FileWorker} performs file I/O operations.
 *
 * @author Brendon
 * @version u0r5, 11/05/2018.
 */
public interface FileWorker {
  /** Returns the path to the working file of this {@code FileWorker} */
  String getPath();
  /** Returns the filename of the working file of this {@code FileWorker} */
  String getFilename();
  /** Sets the path to the working file of this {@code FileWorker} */
  void setPath(String path);
}
