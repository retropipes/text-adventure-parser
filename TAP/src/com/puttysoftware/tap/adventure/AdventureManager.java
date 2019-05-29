/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.adventure;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import com.puttysoftware.tap.Game;
import com.puttysoftware.tap.Messager;
import com.puttysoftware.tap.TAP;

public class AdventureManager {
    // Fields
    private Adventure currentAdventure;
    private boolean loaded;

    // Constructors
    public AdventureManager() {
        this.loaded = false;
    }

    // Methods
    public Adventure getAdventure() {
        return this.currentAdventure;
    }

    public void setAdventure(final Adventure newAdv) {
        this.currentAdventure = newAdv;
    }

    protected void handleDeferredSuccess(final boolean value) {
        this.setLoaded(value);
    }

    public boolean getLoaded() {
        return this.loaded;
    }

    public void setLoaded(final boolean status) {
        this.loaded = status;
    }

    public void loadExampleAdventure() {
        String path = null;
        try {
            path = AdventureManager.class.getProtectionDomain().getCodeSource()
                    .getLocation().toURI().getPath();
            final File folderPath1 = new File(path).getParentFile();
            final String folderPath2 = folderPath1.getAbsolutePath()
                    + File.separator + "Examples";
            final File folderPath3 = new File(folderPath2);
            if (!folderPath3.exists()) {
                path = null;
            } else {
                path = folderPath2;
            }
        } catch (final URISyntaxException use) {
            // Ignore
        }
        this.loadAdventureImpl(path);
    }

    public void loadAdventure() {
        this.loadAdventureImpl(null);
    }

    private void loadAdventureImpl(final String initialDir) {
        final Game game = TAP.getGame();
        String dirname, filename, extension;
        final FileDialog fd = new FileDialog(game.getOutputFrame(),
                "Open Adventure", FileDialog.LOAD);
        if (initialDir != null) {
            fd.setDirectory(initialDir);
        }
        fd.setVisible(true);
        filename = fd.getFile();
        dirname = fd.getDirectory();
        if (filename != null) {
            final File file = new File(dirname + filename);
            extension = AdventureManager.getExtension(file);
            if (extension.equals(Extension.getAdventureExtension())) {
                this.loadFile(dirname + filename);
            } else {
                Messager.showMessage(
                        "You opened something other than an adventure file. Select an adventure file, and try again.");
            }
        }
    }

    private void loadFile(final String filename) {
        final Game game = TAP.getGame();
        final String sg = "Adventure";
        try {
            final File adventureFile = new File(filename);
            final Adventure gameAdventure = new Adventure();
            game.clearCommandOutput();
            gameAdventure.loadAdventure(adventureFile);
            this.setAdventure(gameAdventure);
            this.handleDeferredSuccess(true);
        } catch (final FileNotFoundException fnfe) {
            Messager.showMessage("Loading the " + sg.toLowerCase()
                    + " file failed, probably due to illegal characters in the file name.");
            this.handleDeferredSuccess(false);
        } catch (final IOException ie) {
            Messager.showMessage("Loading the " + sg.toLowerCase()
                    + " file failed, because " + ie.getMessage());
            this.handleDeferredSuccess(false);
        } catch (final Exception ex) {
            TAP.getErrorLogger().logError(ex);
        }
    }

    private static String getExtension(final File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
