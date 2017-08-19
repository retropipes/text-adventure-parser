/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap;

import javax.swing.JOptionPane;

public class Messager {
    public static void showMessage(final String msg) {
        final Game game = TAP.getGame();
        game.getGUIManager().updateCommandOutput(msg);
    }

    public static void showErrorMessage(final String msg) {
        final Game game = TAP.getGame();
        game.getGUIManager().updateCommandOutput("ADVENTURE ERROR: " + msg);
    }

    public static void showTitledDialog(final String msg, final String title) {
        final Game game = TAP.getGame();
        JOptionPane.showMessageDialog(game.getOutputFrame(), msg, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(final String msg, final String title) {
        final Game game = TAP.getGame();
        JOptionPane.showMessageDialog(game.getOutputFrame(), msg, title,
                JOptionPane.ERROR_MESSAGE);
    }

    public static int showConfirmDialog(final String prompt,
            final String title) {
        final Game game = TAP.getGame();
        return JOptionPane.showConfirmDialog(game.getOutputFrame(), prompt,
                title, JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
