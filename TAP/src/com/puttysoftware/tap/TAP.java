/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap;

import org.retropipes.diane.Diane;
import org.retropipes.diane.integration.Integration;

import com.puttysoftware.tap.adventure.AdventureManager;

public class TAP {
    // Constants
    private static Game game;
    private static AdventureManager advMgr;
    private static final String PROGRAM_NAME = "TAP";

    // Methods
    public static AdventureManager getAdventureManager() {
	return TAP.advMgr;
    }

    public static Game getGame() {
	return TAP.game;
    }

    public static void logError(final Throwable t) {
	Diane.handleError(t);
    }

    public static void logNonFatalError(final Throwable t) {
	Diane.handleWarning(t);
    }

    public static void main(final String[] args) {
	try {
	    Diane.installDefaultErrorHandler(PROGRAM_NAME);
	    // Integrate with host platform
	    Integration ni = Integration.integrate();
	    ni.configureLookAndFeel();
	    ni.enableSuddenTermination();
	    ni.setOpenFileHandler(TAP.advMgr);
	    ni.setQuitHandler(new Quitter());
	    // Start game
	    TAP.advMgr = new AdventureManager();
	    TAP.game = new Game();
	    TAP.game.showGUI();
	} catch (final Throwable t) {
	    TAP.logError(t);
	}
    }
}
