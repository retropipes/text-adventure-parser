/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap;

import com.puttysoftware.integration.NativeIntegration;
import com.puttysoftware.sandbox.Sandbox;
import com.puttysoftware.sandbox.SandboxFlag;
import com.puttysoftware.tap.adventure.AdventureManager;
import com.puttysoftware.tap.game.Game;

public class TAP {
    // Constants
    private static Game game;
    private static AdventureManager advMgr;
    private static ErrorHandler errhand;
    private static boolean bigHeadMode = false;

    // Methods
    public static AdventureManager getAdventureManager() {
        return TAP.advMgr;
    }

    public static Game getGame() {
        return TAP.game;
    }

    public static boolean isBigHeadModeEnabled() {
        return TAP.bigHeadMode;
    }

    public static void handleException(final Throwable t) {
        TAP.errhand.handle(t);
    }

    public static void main(final String[] args) {
        // Install error handler
        TAP.errhand = new ErrorHandler();
        Thread.setDefaultUncaughtExceptionHandler(TAP.errhand);
        // Integrate with host platform
        Sandbox sandbox = Sandbox.getSandbox();
        sandbox.cacheFlags();
        TAP.bigHeadMode = sandbox.getFlag(SandboxFlag.CAPS_LOCK);
        final NativeIntegration ni = new NativeIntegration();
        ni.configureLookAndFeel();
        ni.enableSuddenTermination();
        ni.setOpenFileHandler(TAP.advMgr);
        ni.setQuitHandler(new Quitter());
        // Start game
        TAP.advMgr = new AdventureManager();
        TAP.game = new Game();
        TAP.game.showGUI();
    }
}
