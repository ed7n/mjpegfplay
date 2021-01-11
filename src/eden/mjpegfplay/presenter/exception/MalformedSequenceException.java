// @formatter:off
package eden.mjpegfplay.presenter.exception;


/**
 *  A {@code MalformedSequenceException} is thrown when something within a
 *  {@code Sequence} is malformed. Check with the {@code getSubject} and
 *  {@code getProblem} methods for details, and try {@code getRemedy} for a
 *  suggestion on how to resolve it.
 *  <p>
 *  Classes throwing this {@code Exception} must supply all information for the
 *  utmost brevity on error reporting.
 *
 *  @author     Brendon
 *  @version    u0r3, 11/28/2018.
 */
public class MalformedSequenceException extends Exception {

//~~OBJECT CONSTANTS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** The subject that has the problem */
    private final String subject;

    /** The problem that the subject has */
    private final String problem;

    /** Suggested remedy to the problem */
    private final String remedy;


//~~CONSTRUCTORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Makes a {@code MalformedSequenceException} with the given subject,
     *  problem, and remedy.
     */
    public MalformedSequenceException(String subject,
                                      String problem,
                                      String remedy)
    {
        super(subject + ": " + problem);
        this.subject = subject;
        this.problem = problem;
        this.remedy  = remedy;
    }

    /** To prevent uninitialized instantiations of this class */
    private MalformedSequenceException(){
        this.subject = null;
        this.problem = null;
        this.remedy  = null;
    }


//~~~~ACCESSORS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     *  Returns the subject of this {@code MalformedSequenceException} that has
     *  the problem
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     *  Returns the problem of this {@code MalformedSequenceException} that the
     *  subject has
     */
    public String getProblem() {
        return this.problem;
    }

    /**
     *  Returns the suggested remedy to the problem of this {@code
     *  MalformedSequenceException}
     */
    public String getRemedy() {
        return this.remedy;
    }
}
