/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap;

public class Messager {
    public static void showMessage(final String msg) {
        final Game game = TAP.getGame();
        game.updateCommandOutput(msg);
    }

    public static void showErrorMessage(final String msg) {
        final Game game = TAP.getGame();
        game.updateCommandOutput("ADVENTURE ERROR: " + msg);
    }
}
