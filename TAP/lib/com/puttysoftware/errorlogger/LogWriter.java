package com.puttysoftware.errorlogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class LogWriter {
    // Fields
    private static final String MAC_PREFIX = "HOME";
    private static final String WIN_PREFIX = "USERPROFILE";
    private static final String UNIX_PREFIX = "HOME";
    private static final String MAC_DIR = "/Library/Logs/CrashReporter/";
    private static final String WIN_DIR = "\\Crash\\";
    private static final String UNIX_DIR = "/Crash/";
    private static final String MAC_EXT = ".crash";
    private static final String WIN_EXT = ".log";
    private static final String UNIX_EXT = ".log";
    private final Throwable t;
    private final Calendar c;
    private final String p;

    // Constructors
    LogWriter(final Throwable problem, final String programName) {
        this.t = problem;
        this.c = Calendar.getInstance();
        this.p = programName;
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
                    throw new FileNotFoundException("Cannot make directories!");
                }
            }
            // Print to the file
            try (PrintStream s = new PrintStream(
                    new BufferedOutputStream(new FileOutputStream(df)))) {
                this.t.printStackTrace(s);
                s.close();
            }
        } catch (final FileNotFoundException fnf) {
            // Print to standard error, if something went wrong
            this.t.printStackTrace(System.err);
        }
    }

    private static String getErrorDirPrefix() {
        final String osName = System.getProperty("os.name");
        if (osName.indexOf("Mac OS X") != -1) {
            // Mac OS X
            return System.getenv(LogWriter.MAC_PREFIX);
        } else if (osName.indexOf("Windows") != -1) {
            // Windows
            return System.getenv(LogWriter.WIN_PREFIX);
        } else {
            // Other - assume UNIX-like
            return System.getenv(LogWriter.UNIX_PREFIX);
        }
    }

    private static String getErrorDirectory() {
        final String osName = System.getProperty("os.name");
        if (osName.indexOf("Mac OS X") != -1) {
            // Mac OS X
            return LogWriter.MAC_DIR;
        } else if (osName.indexOf("Windows") != -1) {
            // Windows
            return LogWriter.WIN_DIR;
        } else {
            // Other - assume UNIX-like
            return LogWriter.UNIX_DIR;
        }
    }

    private static String getErrorFileExtension() {
        final String osName = System.getProperty("os.name");
        if (osName.indexOf("Mac OS X") != -1) {
            // Mac OS X
            return LogWriter.MAC_EXT;
        } else if (osName.indexOf("Windows") != -1) {
            // Windows
            return LogWriter.WIN_EXT;
        } else {
            // Other - assume UNIX-like
            return LogWriter.UNIX_EXT;
        }
    }

    private String getStampSuffix() {
        final Date time = this.c.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "'_'yyyyMMdd'_'HHmmssSSS");
        return sdf.format(time);
    }

    private String getErrorFileName() {
        return this.p;
    }

    private File getErrorFile() {
        final StringBuilder b = new StringBuilder();
        b.append(LogWriter.getErrorDirPrefix());
        b.append(LogWriter.getErrorDirectory());
        b.append(this.getErrorFileName());
        b.append(this.getStampSuffix());
        b.append(LogWriter.getErrorFileExtension());
        return new File(b.toString());
    }
}
