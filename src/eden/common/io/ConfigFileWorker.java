// @formatter:off
package eden.common.io;


/**
 *  This abstract class contains common fields and methods that defines an EDEN
 *  configuration file. An example of such is as follows:
 *  <p>
 *  <code>config.cfg:                                                       <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;debug:                                          <br>
 *  false                                                                   <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;:debug                                          <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;buffer size:  # in b                            <br>
 *  131072                                                                  <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;:buffer size                                    <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;latency:  # in ms                               <br>
 *  100                                                                     <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;:latency                                        <br>
 *  # If server is much backlogged with client requests, increase latency   <br>
 *  :config.cfg</code>                                                      <br>
 *  <p>
 *  Where {@code config.cfg} is the filename, entry 0 contains the data
 *  {@code false}, entry 1 contains the data {@code 131072}, and so on.
 *  <p>
 *  An entry must contain a header line with a colon (:) appended to its
 *  key, data lines containing its value, and a footer line with a colon
 *  prepended its key starting with a colon. Since colons denote boundaries,
 *  care must be taken when an entry data contains them. An escape sequence
 *  will be implemented in a future release.
 *  <p>
 *  For readability of configuration files, indentation to entry headers and
 *  footers, and empty lines between entries are allowed. Entry data is
 *  read as is, hence--in most cases--they should not be indented.
 *
 *  @author     Brendon
 *  @version    u0r5, 11/05/2018.
 */
public abstract class ConfigFileWorker implements FileWorker {

//~~PROTECTED CLASS CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Denotes an entry's start and end */
    protected static final String LEAD = ":";

    /** Entry header regular expression */
    protected static final String REGEX_KEY =
        "[\\p{Alnum}|[-._]]+" + LEAD + ".*";

    /** Entry indentation {@code String} */
    protected static final String INDENT = "\u0009";


//~~OBJECT FIELDS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Path to working file */
    protected String path;

    /** Name of working file */
    protected String filename;

    /** Lead-in line */
    protected String leadIn;

    /** Lead-out line */
    protected String leadOut;


//~~PUBLIC OBJECT METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//~~~~ACCESSORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public String getPath() {
        return this.path;
    }

    /** @inheritDoc */
    @Override
    public String getFilename() {
        return this.filename;
    }


//~~~~MUTATORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** @inheritDoc */
    @Override
    public void setPath(String path) {
        this.path = path;
        makeStrings();
    }


//~~PROTECTED CLASS METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Makes relevant Strings out of the path to working file of this
     *  ConfigFileWorker
     */
    protected void makeStrings() {
        if (this.path == null) {
            this.filename = null;
            this.leadIn   = null;
            this.leadOut  = null;
            return;
        }
        this.path     = this.path.replace('\\', '/');
        this.filename = this.path.substring(this.path.lastIndexOf('/') + 1);
        this.leadIn   = this.filename + LEAD;
        this.leadOut  = LEAD + this.filename;
    }
}
