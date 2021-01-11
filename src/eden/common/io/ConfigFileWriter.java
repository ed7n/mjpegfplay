// @formatter:off
package eden.common.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 *  A {@code ConfigFileWriter} writes human-readable data to EDEN configuration
 *  files.
 *
 *  @author     Brendon
 *  @version    u0r5, 11/05/2018.
 *
 *  @see        ConfigFileWorker
 */
public class ConfigFileWriter extends ConfigFileWorker implements
    MappedFileWriter,
    IndexedFileWriter
{

//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Makes a {@code ConfigFileWriter} with the given target path */
    public ConfigFileWriter(String path) {
        this.path = path;
        makeStrings();
    }

    /** Makes a {@code ConfigFileWriter} */
    public ConfigFileWriter() {
        this(null);
    }


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public void write(Map<String, String> map) throws
        IllegalArgumentException,
        IOException
    {
        if (map == null) {
            throw new IllegalArgumentException();
        }
        try (final BufferedWriter writer =
                new BufferedWriter(new FileWriter(this.path))
        ) {
            writer.write(this.leadIn);
            mapToFile(writer, map);
            writer.write(this.leadOut);
        }
    }

    /** @inheritDoc */
    @Override
    public void write(List<String> list) throws
        IllegalArgumentException,
        IOException
    {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        try (final BufferedWriter writer =
                new BufferedWriter(new FileWriter(this.path))
        ) {
            writer.write(this.leadIn);
            listToFile(writer, list);
            writer.write(this.leadOut);
        }
    }


//~~PRIVATE OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Writes the contents of map with the given writer */
    private void mapToFile(BufferedWriter writer, Map<String, String> map)
        throws IOException
    {
        writer.newLine();
        writer.newLine();

        for (Map.Entry<String, String> e : map.entrySet()) {
            writer.write(INDENT);
            writer.write(e.getKey());
            writer.write(LEAD);
            writer.newLine();
            writer.write(e.getValue());
            writer.newLine();
            writer.write(INDENT);
            writer.write(LEAD);
            writer.write(e.getKey());
            writer.newLine();
            writer.newLine();
        }
    }

    /** Writes the contents of list with the given writer */
    private void listToFile(BufferedWriter writer, List<String> list)
        throws IOException
    {
        int index = 0;
        writer.newLine();
        writer.newLine();

        for (String s : list) {
            writer.write(INDENT);
            writer.write(Integer.toString(index));
            writer.write(LEAD);
            writer.newLine();
            writer.write(s);
            writer.newLine();
            writer.write(INDENT);
            writer.write(LEAD);
            writer.write(Integer.toString(index));
            writer.newLine();
            writer.newLine();
            index++;
        }
    }
}
