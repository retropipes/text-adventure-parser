package com.puttysoftware.xio;

import java.io.IOException;

public class UnexpectedTagException extends IOException {
    private static final long serialVersionUID = 23250505322336L;

    /**
     * Constructs an instance of <code>UnexpectedTagException</code> with the
     * specified detail message.
     *
     * @param msg
     *            the detail message.
     */
    public UnexpectedTagException(final String msg) {
        super(msg);
    }
}
