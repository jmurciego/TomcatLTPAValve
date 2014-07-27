package org.tomcatltpa.utils;

public class LTPATokenException extends SecurityException{
    
    /**
     * Constructs a <code>LTPATokenException</code> with no detail  message.
     */
    public LTPATokenException() {
        super();
    }

    /**
     * Constructs a <code>LTPATokenException</code> with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public LTPATokenException(String s) {
        super(s);
    }

    /**
     * Creates a <code>LTPATokenException</code> with the specified
     * detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A <tt>null</tt> value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public LTPATokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a <code>LTPATokenException</code> with the specified cause
     * and a detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A <tt>null</tt> value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public LTPATokenException(Throwable cause) {
        super(cause);
    }
}
