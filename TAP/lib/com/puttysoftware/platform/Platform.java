package com.puttysoftware.platform;

import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.UIManager;

import apple.dts.samplecode.osxadapter.OSXAdapter;

public class Platform {
    // Constructor
    private Platform() {
        // Do nothing
    }

    // Methods
    public static void hookLAF(final String programName) {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            // Mac OS X-specific stuff
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    programName);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        } else if (System.getProperty("os.name").startsWith("Windows")) {
            // Windows-specific stuff
            try {
                // Tell the UIManager to use the platform native look and
                // feel
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
                // Hint to the UI that the L&F is decorated
                JFrame.setDefaultLookAndFeelDecorated(true);
            } catch (final Exception e) {
                // Do nothing
            }
        } else {
            // All other platforms
            try {
                // Tell the UIManager to use the Nimbus look and feel
                UIManager.setLookAndFeel(
                        "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                // Hint to the UI that the L&F is decorated
                JFrame.setDefaultLookAndFeelDecorated(true);
            } catch (final Exception e) {
                // Do nothing
            }
        }
    }

    public static void hookFileOpen(final Object o, final Method m,
            final String s) {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            OSXAdapter.setFileHandler(o, m);
        } else {
            try {
                m.invoke(o, s);
            } catch (final IllegalArgumentException e) {
                // Ignore
            } catch (final IllegalAccessException e) {
                // Ignore
            } catch (final InvocationTargetException e) {
                // Ignore
            }
        }
    }

    public static void hookQuit(final Object o, final Method m) {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            OSXAdapter.setQuitHandler(o, m);
        }
    }

    public static void hookPreferences(final Object o, final Method m) {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            OSXAdapter.setPreferencesHandler(o, m);
        }
    }

    public static void hookAbout(final Object o, final Method m) {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            OSXAdapter.setAboutHandler(o, m);
        }
    }

    public static void hookFullScreen(final Window w, final boolean fsCapable) {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            OSXAdapter.setWindowCanFullScreen(w, fsCapable);
        }
    }

    public static void hookDockIcon(final Image i) {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            OSXAdapter.setDockIconImage(i);
        }
    }

    public static void hookFrameIcon(final Frame f, final Image i) {
        if (!System.getProperty("os.name").startsWith("Mac OS X")) {
            f.setIconImage(i);
        }
    }

    public static void hookDockIconBadge(final String badgeText) {
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            OSXAdapter.setDockIconBadge(badgeText);
        }
    }
}
