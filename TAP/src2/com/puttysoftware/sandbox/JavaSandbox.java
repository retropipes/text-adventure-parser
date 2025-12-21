package com.puttysoftware.sandbox;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;

final class JavaSandbox extends Sandbox {
    // Constants
    private static final String APP_SUPPORT_FALLBACK_DIR = "Support"; //$NON-NLS-1$
    private static final String AUTOSAVE_FALLBACK_DIR = "Autosave"; //$NON-NLS-1$
    private static final String CACHES_FALLBACK_DIR = "Caches"; //$NON-NLS-1$
    private static final String DESKTOP_FALLBACK_DIR = "Desktop"; //$NON-NLS-1$
    private static final String DOCUMENTS_FALLBACK_DIR = "Documents"; //$NON-NLS-1$
    private static final String DOWNLOADS_FALLBACK_DIR = "Downloads"; //$NON-NLS-1$
    private static final String FALLBACK_PREFIX = "HOME"; //$NON-NLS-1$
    private static final String LIBRARY_FALLBACK_DIR = ".config/sandboxes/com.puttysoftware.tap"; //$NON-NLS-1$
    private static final String MOVIES_FALLBACK_DIR = "Movies"; //$NON-NLS-1$
    private static final String MUSIC_FALLBACK_DIR = "Music"; //$NON-NLS-1$
    private static final String PICTURES_FALLBACK_DIR = "Pictures"; //$NON-NLS-1$
    private static final String SHARED_PUBLIC_FALLBACK_DIR = "SharedPublic"; //$NON-NLS-1$
    // Fields
    private HashMap<SandboxFlag, Boolean> flagCache;

    // Constructor
    JavaSandbox() {
        super();
        this.flagCache = new HashMap<>();
    }

    // Methods
    @Override
    protected String getDirectory(final SystemDir dir) {
        switch (dir) {
            case APPLICATION:
            case SYSTEM_APPLICATION:
                return JavaSandbox.getLibraryFallbackDirectory();
            case APPLICATION_SUPPORT:
            case SYSTEM_APPLICATION_SUPPORT:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.APP_SUPPORT_FALLBACK_DIR;
            case AUTOSAVED_INFORMATION:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.AUTOSAVE_FALLBACK_DIR;
            case CACHES:
            case SYSTEM_CACHES:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.CACHES_FALLBACK_DIR;
            case DOCUMENTS:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.DOCUMENTS_FALLBACK_DIR;
            case DESKTOP:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.DESKTOP_FALLBACK_DIR;
            case DOWNLOADS:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.DOWNLOADS_FALLBACK_DIR;
            case LIBRARY:
            case SYSTEM_LIBRARY:
                return JavaSandbox.getLibraryFallbackDirectory();
            case MOVIES:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.MOVIES_FALLBACK_DIR;
            case MUSIC:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.MUSIC_FALLBACK_DIR;
            case PICTURES:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator + JavaSandbox.PICTURES_FALLBACK_DIR;
            case SHARED_PUBLIC:
                return JavaSandbox.getLibraryFallbackDirectory()
                        + File.pathSeparator
                        + JavaSandbox.SHARED_PUBLIC_FALLBACK_DIR;
            case SYSTEM_USER:
            case USER_HOME:
                return System.getProperty("user.home"); //$NON-NLS-1$
            default:
                return JavaSandbox.getLibraryFallbackDirectory();
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
        return System.getenv(JavaSandbox.FALLBACK_PREFIX) + File.pathSeparator
                + JavaSandbox.LIBRARY_FALLBACK_DIR;
    }
}
