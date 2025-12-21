package com.puttysoftware.sandbox;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;

final class WindowsSandbox extends Sandbox {
    // Constants
    private static final String APP_SUPPORT_FALLBACK_DIR = "Support"; //$NON-NLS-1$
    private static final String AUTOSAVE_FALLBACK_DIR = "Autosave"; //$NON-NLS-1$
    private static final String CACHES_FALLBACK_DIR = "Caches"; //$NON-NLS-1$
    private static final String DESKTOP_FALLBACK_DIR = "Desktop"; //$NON-NLS-1$
    private static final String DOCUMENTS_FALLBACK_DIR = "Documents"; //$NON-NLS-1$
    private static final String DOWNLOADS_FALLBACK_DIR = "Downloads"; //$NON-NLS-1$
    private static final String FALLBACK_PREFIX = "APPDATA"; //$NON-NLS-1$
    private static final String LIBRARY_FALLBACK_DIR = "Sandboxes\\com.puttysoftware.tap"; //$NON-NLS-1$
    private static final String MOVIES_FALLBACK_DIR = "Movies"; //$NON-NLS-1$
    private static final String MUSIC_FALLBACK_DIR = "Music"; //$NON-NLS-1$
    private static final String PICTURES_FALLBACK_DIR = "Pictures"; //$NON-NLS-1$
    private static final String SHARED_PUBLIC_FALLBACK_DIR = "SharedPublic"; //$NON-NLS-1$
    // Fields
    private HashMap<SandboxFlag, Boolean> flagCache;

    // Constructor
    WindowsSandbox() {
        super();
        this.flagCache = new HashMap<>();
    }

    // Methods
    @Override
    protected String getDirectory(final SystemDir dir) {
        switch (dir) {
            case APPLICATION:
            case SYSTEM_APPLICATION:
                return WindowsSandbox.getLibraryFallbackDirectory();
            case APPLICATION_SUPPORT:
            case SYSTEM_APPLICATION_SUPPORT:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator
                        + WindowsSandbox.APP_SUPPORT_FALLBACK_DIR;
            case AUTOSAVED_INFORMATION:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + WindowsSandbox.AUTOSAVE_FALLBACK_DIR;
            case CACHES:
            case SYSTEM_CACHES:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + WindowsSandbox.CACHES_FALLBACK_DIR;
            case DOCUMENTS:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator
                        + WindowsSandbox.DOCUMENTS_FALLBACK_DIR;
            case DESKTOP:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + WindowsSandbox.DESKTOP_FALLBACK_DIR;
            case DOWNLOADS:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator
                        + WindowsSandbox.DOWNLOADS_FALLBACK_DIR;
            case LIBRARY:
            case SYSTEM_LIBRARY:
                return WindowsSandbox.getLibraryFallbackDirectory();
            case MOVIES:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + WindowsSandbox.MOVIES_FALLBACK_DIR;
            case MUSIC:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + WindowsSandbox.MUSIC_FALLBACK_DIR;
            case PICTURES:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + WindowsSandbox.PICTURES_FALLBACK_DIR;
            case SHARED_PUBLIC:
                return WindowsSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator
                        + WindowsSandbox.SHARED_PUBLIC_FALLBACK_DIR;
            case SYSTEM_USER:
            case USER_HOME:
                return System.getProperty("user.home"); //$NON-NLS-1$
            default:
                return WindowsSandbox.getLibraryFallbackDirectory();
        }
    }

    @Override
    public boolean getFlag(final SandboxFlag flag) {
        return this.flagCache.getOrDefault(flag, false);
    }

    @Override
    public void cacheFlags() {
        this.flagCache.put(SandboxFlag.CAPS_LOCK, Toolkit.getDefaultToolkit()
                .getLockingKeyState(KeyEvent.VK_CAPS_LOCK));
    }

    private static String getLibraryFallbackDirectory() {
        return System.getenv(WindowsSandbox.FALLBACK_PREFIX)
                + File.pathSeparator + WindowsSandbox.LIBRARY_FALLBACK_DIR;
    }
}
