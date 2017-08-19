/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap;

import com.puttysoftware.errorlogger.ErrorLogger;
import com.puttysoftware.platform.Platform;

public class TAP {
    // Constants
    private static Game game;
    private static final String PROGRAM_NAME = "TAP";
    private static final ErrorLogger debug = new ErrorLogger(TAP.PROGRAM_NAME);

    // Methods
    public static Game getGame() {
        return TAP.game;
    }

    public static ErrorLogger getErrorLogger() {
        return TAP.debug;
    }

    public static void main(final String[] args) {
        try {
            // Integrate with host platform
            Platform.hookLAF(TAP.PROGRAM_NAME);
            TAP.game = new Game();
            TAP.game.postConstruct();
            TAP.game.getGUIManager().showGUI();
            // Register platform hooks
            Platform.hookQuit(TAP.getGame().getGUIManager(),
                    TAP.getGame().getGUIManager().getClass()
                            .getDeclaredMethod("quitHandler"));
        } catch (final Throwable t) {
            TAP.getErrorLogger().logError(t);
        }
    }
}
