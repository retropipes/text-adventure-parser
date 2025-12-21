package com.puttysoftware.sandbox;

import java.io.File;

public abstract class Sandbox {
    // Fields
    private static Sandbox SANDBOX;

    // Constructor
    protected Sandbox() {
        new File(this.getDirectory(SystemDir.LIBRARY)).mkdirs();
        new File(this.getDirectory(SystemDir.APPLICATION_SUPPORT)).mkdirs();
        new File(this.getDirectory(SystemDir.DOCUMENTS)).mkdirs();
    }

    // Static methods
    public static Sandbox getSandbox() {
        if (Sandbox.SANDBOX == null) {
            final String osName = System.getProperty("os.name"); //$NON-NLS-1$
            if ("Mac OS X".equals(osName)) { //$NON-NLS-1$
                Sandbox.SANDBOX = new MacOSSandbox();
            } else if (osName.startsWith("Windows")) { //$NON-NLS-1$
                Sandbox.SANDBOX = new WindowsSandbox();
            } else {
                Sandbox.SANDBOX = new JavaSandbox();
            }
        }
        return Sandbox.SANDBOX;
    }

    // Methods
    public final String getDocumentsDirectory() {
        return this.getDirectory(SystemDir.DOCUMENTS);
    }

    public final String getSupportDirectory() {
        return this.getDirectory(SystemDir.APPLICATION_SUPPORT);
    }

    protected abstract String getDirectory(final SystemDir dir);

    public abstract boolean getFlag(final SandboxFlag flag);

    public abstract void cacheFlags();
}
