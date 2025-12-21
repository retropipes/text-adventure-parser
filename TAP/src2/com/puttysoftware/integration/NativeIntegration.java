/* Native Integration for Java Programs Library
Licensed under Apache 2.0. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/wrldwzrd89/lib-java-native-integration
 */
package com.puttysoftware.integration;

import java.awt.Desktop;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.AppForegroundListener;
import java.awt.desktop.AppHiddenListener;
import java.awt.desktop.AppReopenedListener;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.OpenURIHandler;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.PrintFilesHandler;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitStrategy;
import java.awt.desktop.ScreenSleepListener;
import java.awt.desktop.SystemSleepListener;
import java.awt.desktop.UserSessionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

public class NativeIntegration {
    // Fields
    private final Desktop desktop;
    private final boolean supported;
    private final boolean macOS;
    private final boolean windows;
    private final boolean otherOS;

    // Constructor
    public NativeIntegration() {
        this.supported = Desktop.isDesktopSupported();
        if (this.supported) {
            this.desktop = Desktop.getDesktop();
        } else {
            this.desktop = null;
        }
        final String osName = System.getProperty("os.name"); //$NON-NLS-1$
        if ("Mac OS X".equals(osName)) { //$NON-NLS-1$
            this.macOS = true;
            this.windows = false;
            this.otherOS = false;
        } else if (osName.startsWith("Windows")) { //$NON-NLS-1$
            this.macOS = false;
            this.windows = true;
            this.otherOS = false;
        } else {
            this.macOS = false;
            this.windows = false;
            this.otherOS = true;
        }
    }

    public final boolean isMacOS() {
        return this.macOS;
    }

    public final boolean isWindows() {
        return this.windows;
    }

    public final boolean isOtherOS() {
        return this.otherOS;
    }

    public void addAppForegroundListener(final AppForegroundListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_FOREGROUND)) {
            this.desktop.addAppEventListener(listener);
        }
    }

    public void addAppHiddenListener(final AppHiddenListener listener) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_EVENT_HIDDEN)) {
            this.desktop.addAppEventListener(listener);
        }
    }

    public void addAppReopenedListener(final AppReopenedListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_REOPENED)) {
            this.desktop.addAppEventListener(listener);
        }
    }

    public void addScreenSleepListener(final ScreenSleepListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_SCREEN_SLEEP)) {
            this.desktop.addAppEventListener(listener);
        }
    }

    public void addSystemSleepListener(final SystemSleepListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_SYSTEM_SLEEP)) {
            this.desktop.addAppEventListener(listener);
        }
    }

    public void addUserSessionListener(final UserSessionListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_USER_SESSION)) {
            this.desktop.addAppEventListener(listener);
        }
    }

    public void browse(final URI url) throws IOException {
        if (this.supported && this.desktop.isSupported(Desktop.Action.BROWSE)) {
            this.desktop.browse(url);
        }
    }

    public void browseFileDirectory(final File file) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
            this.desktop.browseFileDirectory(file);
        }
    }

    // Methods
    public void configureLookAndFeel() {
        if (this.macOS) {
            // macOS-specific stuff
            try {
                // Tell the UIManager to use the native look and feel
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (final Exception e) {
                // Do nothing
            }
        } else if (this.windows) {
            // Windows-specific stuff
            try {
                // Tell the UIManager to use the native look and feel
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
                // Tell the UIManager to use the Cross Platform look and feel
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName());
                // Hint to the UI that the L&F is decorated
                JFrame.setDefaultLookAndFeelDecorated(true);
            } catch (final Exception e) {
                // Do nothing
            }
        }
    }

    public void disableSuddenTermination() {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_SUDDEN_TERMINATION)) {
            this.desktop.disableSuddenTermination();
        }
    }

    public void edit(final File file) throws IOException {
        if (this.supported && this.desktop.isSupported(Desktop.Action.EDIT)) {
            this.desktop.edit(file);
        }
    }

    public void enableSuddenTermination() {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_SUDDEN_TERMINATION)) {
            this.desktop.enableSuddenTermination();
        }
    }

    public void mail() throws IOException {
        if (this.supported && this.desktop.isSupported(Desktop.Action.MAIL)) {
            this.desktop.mail();
        }
    }

    public void mail(final URI mailto) throws IOException {
        if (this.supported && this.desktop.isSupported(Desktop.Action.MAIL)) {
            this.desktop.mail(mailto);
        }
    }

    public void moveToTrash(final File file) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.MOVE_TO_TRASH)) {
            this.desktop.moveToTrash(file);
        }
    }

    public void open(final File file) throws IOException {
        if (this.supported && this.desktop.isSupported(Desktop.Action.OPEN)) {
            this.desktop.open(file);
        }
    }

    public void openHelpViewer() {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_HELP_VIEWER)) {
            this.desktop.openHelpViewer();
        }
    }

    public void print(final File file) throws IOException {
        if (this.supported && this.desktop.isSupported(Desktop.Action.PRINT)) {
            this.desktop.print(file);
        }
    }

    public void removeAppForegroundListener(
            final AppForegroundListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_FOREGROUND)) {
            this.desktop.removeAppEventListener(listener);
        }
    }

    public void removeAppHiddenListener(final AppHiddenListener listener) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_EVENT_HIDDEN)) {
            this.desktop.removeAppEventListener(listener);
        }
    }

    public void removeAppReopenedListener(final AppReopenedListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_REOPENED)) {
            this.desktop.removeAppEventListener(listener);
        }
    }

    public void removeScreenSleepListener(final ScreenSleepListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_SCREEN_SLEEP)) {
            this.desktop.removeAppEventListener(listener);
        }
    }

    public void removeSystemSleepListener(final SystemSleepListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_SYSTEM_SLEEP)) {
            this.desktop.removeAppEventListener(listener);
        }
    }

    public void removeUserSessionListener(final UserSessionListener listener) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_EVENT_USER_SESSION)) {
            this.desktop.removeAppEventListener(listener);
        }
    }

    public void requestForeground(final boolean allWindows) {
        if (this.supported && this.desktop
                .isSupported(Desktop.Action.APP_REQUEST_FOREGROUND)) {
            this.desktop.requestForeground(allWindows);
        }
    }

    public void setAboutHandler(final AboutHandler aboutHandler) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_ABOUT)) {
            this.desktop.setAboutHandler(aboutHandler);
        }
    }

    public void setDefaultMenuBar(final JMenuBar defaultMenuBar) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_MENU_BAR)) {
            this.desktop.setDefaultMenuBar(defaultMenuBar);
        }
    }

    public void setOpenFileHandler(final OpenFilesHandler openFileHandler) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_OPEN_FILE)) {
            this.desktop.setOpenFileHandler(openFileHandler);
        }
    }

    public void setOpenURIHandler(final OpenURIHandler openURIHandler) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
            this.desktop.setOpenURIHandler(openURIHandler);
        }
    }

    public void setPreferencesHandler(
            final PreferencesHandler preferencesHandler) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_PREFERENCES)) {
            this.desktop.setPreferencesHandler(preferencesHandler);
        }
    }

    public void setPrintFileHandler(final PrintFilesHandler printFileHandler) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_PRINT_FILE)) {
            this.desktop.setPrintFileHandler(printFileHandler);
        }
    }

    public void setQuitHandler(final QuitHandler quitHandler) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
            this.desktop.setQuitHandler(quitHandler);
        }
    }

    public void setQuitStrategy(final QuitStrategy quitStrategy) {
        if (this.supported
                && this.desktop.isSupported(Desktop.Action.APP_QUIT_STRATEGY)) {
            this.desktop.setQuitStrategy(quitStrategy);
        }
    }
}
