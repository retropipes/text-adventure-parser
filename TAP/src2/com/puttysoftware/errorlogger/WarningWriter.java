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

class WarningWriter {
    // Fields
    private static final String EXT = ".warning.log"; //$NON-NLS-1$
    private final Throwable exception;
    private final String program;
    private final Date time;

    // Constructors
    WarningWriter(final Throwable problem, final String programName,
            final Calendar timeSource) {
        this.exception = problem;
        this.program = programName;
        this.time = timeSource.getTime();
    }

    // Methods
    void writeLogInfo() {
        try {
            // Make sure the needed directories exist first
            final File df = this.getLogFile();
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

    private static String getLogDirectory() {
        return Sandbox.getSandbox().getSupportDirectory();
    }

    private String getStampSuffix() {
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "'_'yyyyMMdd'_'HHmmssSSS"); //$NON-NLS-1$
        return sdf.format(this.time);
    }

    private String getLogFileName() {
        return this.program;
    }

    private File getLogFile() {
        final StringBuilder b = new StringBuilder();
        b.append(WarningWriter.getLogDirectory());
        b.append(this.getLogFileName());
        b.append(this.getStampSuffix());
        b.append(WarningWriter.EXT);
        return new File(b.toString());
    }
}
