package eden.common.io;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A {@code ConfigFileReader} reads human-readable data from EDEN configuration
 * files.
 *
 * @author Brendon
 * @version u0r5, 11/05/2018.
 *
 * @see ConfigFileWorker
 */
public class ConfigFileReader extends ConfigFileWorker implements
    MappedFileReader,
    IndexedFileReader {

  /** Makes a {@code ConfigFileReader} with the given target path */
  public ConfigFileReader(String path) {
    this.path = path;
    makeStrings();
  }

  /** Makes a {@code ConfigFileReader} */
  public ConfigFileReader() {
    this(null);
  }

  /** {@inheritDoc} */
  @Override
  public String read(String key) throws
      IllegalArgumentException,
      IOException,
      NoSuchElementException {
    if (key == null)
      throw new IllegalArgumentException();
    Integer row = 0;
    boolean foundKey = false;

    try (final BufferedReader reader = new BufferedReader(new FileReader(
        this.path))) {
      validate(reader, row);
      seekToKey(reader, row, key);
      foundKey = true;
      return getValue(reader, row, key);
    } catch (NullPointerException e) {
      if (foundKey)
        throw new EOFException(this.path + ':' + row);
      else
        throw new NoSuchElementException(key);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, String> readToMap() throws IOException {
    Map<String, String> out;
    Integer row = 0;
    String currentKey;

    try (final BufferedReader reader = new BufferedReader(new FileReader(
        this.path))) {
      validate(reader, row);
      out = new HashMap<>();

      while (true) {
        currentKey = seekToNextKey(reader, row);

        if (currentKey == null)
          break;
        out.put(currentKey, getValue(reader, row, currentKey));
      }
      return out;
    } catch (NullPointerException e) {
      throw new EOFException(this.path + ':' + row);
    }
  }

  /** {@inheritDoc} */
  @Override
  public String read(int index) throws
      IllegalArgumentException,
      IOException,
      IndexOutOfBoundsException {
    if (index < 0)
      throw new IllegalArgumentException();
    Integer row = 0;
    String key;
    boolean foundKey = false;

    try (final BufferedReader reader = new BufferedReader(new FileReader(
        this.path))) {
      validate(reader, row);
      key = seekToIndex(reader, row, index);
      foundKey = true;
      return getValue(reader, row, key);
    } catch (NullPointerException e) {
      if (foundKey)
        throw new EOFException(this.path + ':' + row);
      else
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }
  }

  /** {@inheritDoc} */
  @Override
  public List<String> readToList() throws
      IllegalArgumentException,
      IOException {
    if (path == null)
      throw new IllegalArgumentException();
    List<String> out;
    Integer row = 0;
    String currentKey;

    try (final BufferedReader reader = new BufferedReader(new FileReader(
        this.path))) {
      validate(reader, row);
      out = new ArrayList<>();

      while (true) {
        currentKey = seekToNextKey(reader, row);

        if (currentKey == null)
          break;
        out.add(getValue(reader, row, currentKey));
      }
      return out;
    } catch (NullPointerException e) {
      throw new EOFException(path + ':' + row);
    }
  }

  /**
   * Validates the given configuration file by reading its lead-in signature.
   * This is a way to guarantee that the file is formatted appropriately
   */
  private void validate(BufferedReader reader, Integer row) throws
      IOException {
    row++;

    if (!reader.readLine().equals(this.leadIn))
      throw new IOException(this.path + ':' + row + " Bad signature");
  }

  /** Continues seeking the given reader to the given key */
  private void seekToKey(BufferedReader reader,
      Integer row,
      String key)
      throws IOException {
    String str;

    do {
      do {
        str = reader.readLine().trim();
        row++;
      } while (!str.matches(REGEX_KEY));

      str = str.substring(0, str.indexOf(LEAD));
    } while (!str.equals(key));
  }

  /** Continues seeking the given reader to a key */
  private String seekToNextKey(BufferedReader reader, Integer row) throws
      IOException {
    String str;

    while (true) {
      str = reader.readLine().trim();
      row++;

      if (str.matches(REGEX_KEY))
        return str.substring(0, str.indexOf(LEAD));
      else if (str.matches(this.leadOut))
        return null;
    }
  }

  /** Continues seeking the given reader to a given index */
  private String seekToIndex(BufferedReader reader,
      Integer row,
      int index)
      throws IOException {
    String str;

    while (true) {
      do {
        str = reader.readLine().trim();
        row++;
      } while (!str.matches(REGEX_KEY));

      if (--index < 0)
        break;
      skipEntry(reader, row, str.substring(0, str.indexOf(LEAD)));
    }
    return str.substring(0, str.indexOf(LEAD));
  }

  /** Reads and returns the value mapped to the given key */
  private String getValue(BufferedReader reader,
      Integer row,
      String key)
      throws IOException {
    StringBuilder out = new StringBuilder();
    String str = reader.readLine();
    row++;

    while (!str.trim().matches(LEAD + key + ".*")) {
      out.append(str);
      str = reader.readLine();
      row++;
    }
    return out.toString();
  }

  /** Skips an entry value */
  private void skipEntry(BufferedReader reader,
      Integer row,
      String key)
      throws IOException {
    String str;

    do {
      str = reader.readLine();
      row++;
    } while (!str.trim().matches(LEAD + key + ".*"));
  }
}
