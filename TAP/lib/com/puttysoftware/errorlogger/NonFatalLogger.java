package com.puttysoftware.errorlogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class NonFatalLogger {
    // Fields
    private static final String MAC_PREFIX = "HOME";
    private static final String WIN_PREFIX = "USERPROFILE";
    private static final String UNIX_PREFIX = "HOME";
    private static final String MAC_DIR = "/Library/Logs/CrashReporter/";
    private static final String WIN_DIR = "\\Crash\\";
    private static final String UNIX_DIR = "/Crash/";
    private static final String MAC_EXT = ".nonfatal";
    private static final String WIN_EXT = ".nonfatal";
    private static final String UNIX_EXT = ".nonfatal";
    private final Throwable t;
    private final Calendar c;
    private final String p;

    // Constructors
    NonFatalLogger(final Throwable problem, final String programName) {
        this.t = problem;
        this.c = Calendar.getInstance();
        this.p = programName;
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

    private static String getLogDirPrefix() {
        final String osName = System.getProperty("os.name");
        if (osName.indexOf("Mac OS X") != -1) {
            // Mac OS X
            return System.getenv(NonFatalLogger.MAC_PREFIX);
        } else if (osName.indexOf("Windows") != -1) {
            // Windows
            return System.getenv(NonFatalLogger.WIN_PREFIX);
        } else {
            // Other - assume UNIX-like
            return System.getenv(NonFatalLogger.UNIX_PREFIX);
        }
    }

    private static String getLogDirectory() {
        final String osName = System.getProperty("os.name");
        if (osName.indexOf("Mac OS X") != -1) {
            // Mac OS X
            return NonFatalLogger.MAC_DIR;
        } else if (osName.indexOf("Windows") != -1) {
            // Windows
            return NonFatalLogger.WIN_DIR;
        } else {
            // Other - assume UNIX-like
            return NonFatalLogger.UNIX_DIR;
        }
    }

    private static String getLogFileExtension() {
        final String osName = System.getProperty("os.name");
        if (osName.indexOf("Mac OS X") != -1) {
            // Mac OS X
            return NonFatalLogger.MAC_EXT;
        } else if (osName.indexOf("Windows") != -1) {
            // Windows
            return NonFatalLogger.WIN_EXT;
        } else {
            // Other - assume UNIX-like
            return NonFatalLogger.UNIX_EXT;
        }
    }

    private String getStampSuffix() {
        final Date time = this.c.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "'_'yyyyMMdd'_'HHmmssSSS");
        return sdf.format(time);
    }

    private String getLogFileName() {
        return this.p;
    }

    private File getLogFile() {
        final StringBuilder b = new StringBuilder();
        b.append(NonFatalLogger.getLogDirPrefix());
        b.append(NonFatalLogger.getLogDirectory());
        b.append(this.getLogFileName());
        b.append(this.getStampSuffix());
        b.append(NonFatalLogger.getLogFileExtension());
        return new File(b.toString());
    }
}
