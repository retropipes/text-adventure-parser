/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap;

import javax.swing.JFrame;

import com.puttysoftware.tap.adventure.AdventureManager;

public class Game {
    // Fields
    private AdventureManager AdventureMgr;
    private GUIManager guiMgr;

    // Constructors
    public Game() {
        // Do nothing
    }

    // Methods
    void postConstruct() {
        // Create Managers
        this.guiMgr = new GUIManager();
    }

    public GUIManager getGUIManager() {
        return this.guiMgr;
    }

    public AdventureManager getAdventureManager() {
        if (this.AdventureMgr == null) {
            this.AdventureMgr = new AdventureManager();
        }
        return this.AdventureMgr;
    }

    public JFrame getOutputFrame() {
        return this.getGUIManager().getGUIFrame();
    }
}
