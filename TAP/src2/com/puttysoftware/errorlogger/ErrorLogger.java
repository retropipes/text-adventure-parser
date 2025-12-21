package com.puttysoftware.errorlogger;

import java.util.Calendar;

/**
 * Error Logger: record exceptions from Java programs into log files.
 */
public final class ErrorLogger {
    /** The Constant calendar. */
    // Fields
    private static final Calendar calendar = Calendar.getInstance();
    /** The program name. */
    private final String name;

    /**
     * Instantiates a new error logger.
     *
     * @param programName
     *                    the program name
     */
    // Constructor
    public ErrorLogger(final String programName) {
        this.name = programName;
    }

    /**
     * Log an error and terminate execution.
     *
     * @param e
     *          the exception to log
     */
    // Methods
    public void logError(final Throwable e) {
        final ErrorWriter ew = new ErrorWriter(e, this.name,
                ErrorLogger.calendar);
        ew.writeErrorInfo();
        System.exit(1);
    }

    /**
     * Log a warning.
     *
     * @param e
     *          the exception to log
     */
    public void logWarning(final Throwable e) {
        final WarningWriter ww = new WarningWriter(e, this.name,
                ErrorLogger.calendar);
        ww.writeLogInfo();
    }
}
