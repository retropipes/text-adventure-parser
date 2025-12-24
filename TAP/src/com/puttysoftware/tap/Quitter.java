package com.puttysoftware.tap;

import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;

class Quitter implements QuitHandler {
    public Quitter() {
	super();
    }

    @Override
    public void handleQuitRequestWith(QuitEvent inE, QuitResponse inResponse) {
	System.exit(0);
    }
}
