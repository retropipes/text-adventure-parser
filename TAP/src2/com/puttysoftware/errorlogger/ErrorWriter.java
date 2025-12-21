package com.puttysoftware.errorlogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.puttysoftware.sandbox.Sandbox;

class ErrorWriter {
    // Fields
    private static final String EXT = ".error.log"; //$NON-NLS-1$
    private final Throwable exception;
    private final Date time;
    private final String program;

    // Constructors
    ErrorWriter(final Throwable problem, final String programName,
            final Calendar timeSource) {
        this.exception = problem;
        this.time = timeSource.getTime();
        this.program = programName;
    }

    // Methods
    void writeErrorInfo() {
        try {
            // Make sure the needed directories exist first
            final File df = this.getErrorFile();
            final File parent = new File(df.getParent());
            if (!parent.exists()) {
                final boolean res = parent.mkdirs();
                if (!res) {
                    throw new FileNotFoundException("Cannot make directories!"); //$NON-NLS-1$
                }
            }
            // Print to the file
            try (PrintStream s = new PrintStream(
                    new BufferedOutputStream(new FileOutputStream(df)))) {
                this.exception.printStackTrace(s);
                s.close();
            }
        } catch (final FileNotFoundException fnf) {
            // Print to standard error, if something went wrong
            this.exception.printStackTrace(System.err);
        }
    }

    private static String getErrorDirectory() {
        return Sandbox.getSandbox().getSupportDirectory();
    }

    private String getStampSuffix() {
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "'_'yyyyMMdd'_'HHmmssSSS"); //$NON-NLS-1$
        return sdf.format(this.time);
    }

    private String getErrorFileName() {
        return this.program;
    }

    private File getErrorFile() {
        final StringBuilder b = new StringBuilder();
        b.append(ErrorWriter.getErrorDirectory());
        b.append(this.getErrorFileName());
        b.append(this.getStampSuffix());
        b.append(ErrorWriter.EXT);
        return new File(b.toString());
    }
}
