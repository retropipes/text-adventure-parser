/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.adventure;

import java.awt.FileDialog;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.tap.Game;
import com.puttysoftware.tap.Messager;
import com.puttysoftware.tap.TAP;

public class AdventureManager implements OpenFilesHandler {
    // Fields
    private Adventure currentAdventure;
    private boolean loaded;
    private String[] exampleFiles;
    private String[] exampleFileList;
    private static final String LIST_FILE = "/examples/examples.list";
    private static final String DISPLAY_LIST_FILE = "/examples/examples.display";

    // Constructors
    public AdventureManager() {
        this.loaded = false;
        this.exampleFiles = null;
        this.exampleFileList = null;
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
        try {
            this.loadLists();
            int index = this.getAdventureIndex();
            if (index != -1) {
                String exName = "/examples/" + this.exampleFiles[index];
                ArrayList<String> data = new ArrayList<>();
                try (InputStream is = AdventureManager.class
                        .getResourceAsStream(exName);
                        ResourceStreamReader ris = new ResourceStreamReader(
                                is)) {
                    String line = "";
                    while (line != null) {
                        line = ris.readString();
                        if (line != null) {
                            data.add(line);
                        }
                    }
                }
                this.loadExampleData(data);
            }
        } catch (final IOException ioe) {
            Messager.showMessage(
                    "Loading examples failed, because a file I/O error occurred.");
            TAP.getErrorLogger().logNonFatalError(ioe);
        }
    }

    private int getAdventureIndex() {
        String choice = CommonDialogs.showInputDialog("Which Adventure?",
                "Load Example Adventure", this.exampleFileList,
                this.exampleFileList[0]);
        if (choice != null && !choice.isEmpty()) {
            for (int i = 0; i < this.exampleFileList.length; i++) {
                if (this.exampleFileList[i].equals(choice)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void loadLists() throws IOException {
        if (this.exampleFiles == null) {
            ArrayList<String> exampleFilesAL = new ArrayList<>();
            try (InputStream is = AdventureManager.class
                    .getResourceAsStream(LIST_FILE);
                    ResourceStreamReader ris = new ResourceStreamReader(is)) {
                String line = "";
                while (line != null) {
                    line = ris.readString();
                    if (line != null) {
                        exampleFilesAL.add(line);
                    }
                }
            }
            this.exampleFiles = exampleFilesAL
                    .toArray(new String[exampleFilesAL.size()]);
        }
        if (this.exampleFileList == null) {
            ArrayList<String> exampleFileListAL = new ArrayList<>();
            try (InputStream is = AdventureManager.class
                    .getResourceAsStream(DISPLAY_LIST_FILE);
                    ResourceStreamReader ris = new ResourceStreamReader(is)) {
                String line = "";
                while (line != null) {
                    line = ris.readString();
                    if (line != null) {
                        exampleFileListAL.add(line);
                    }
                }
            }
            this.exampleFileList = exampleFileListAL
                    .toArray(new String[exampleFileListAL.size()]);
        }
    }

    private void loadExampleData(final ArrayList<String> adventureData) {
        final Game game = TAP.getGame();
        final Adventure gameAdventure = new Adventure();
        game.clearCommandOutput();
        gameAdventure.loadExampleAdventure(adventureData);
        this.setAdventure(gameAdventure);
        this.handleDeferredSuccess(true);
    }

    public void loadAdventure() {
        final Game game = TAP.getGame();
        String dirname, filename, extension;
        final FileDialog fd = new FileDialog(game.getOutputFrame(),
                "Open Adventure", FileDialog.LOAD);
        fd.setVisible(true);
        filename = fd.getFile();
        dirname = fd.getDirectory();
        if (filename != null) {
            final File file = new File(dirname + filename);
            extension = AdventureManager.getExtension(file);
            if (extension.equals(Extension.getAdventureExtension())) {
                this.loadAdventureFile(dirname + filename);
            } else {
                Messager.showMessage(
                        "You opened something other than an adventure file. Select an adventure file, and try again.");
            }
        }
    }

    private void loadAdventureFile(final String filename) {
        final Game game = TAP.getGame();
        try {
            final File adventureFile = new File(filename);
            final Adventure gameAdventure = new Adventure();
            game.clearCommandOutput();
            gameAdventure.loadAdventure(adventureFile);
            this.setAdventure(gameAdventure);
            this.handleDeferredSuccess(true);
        } catch (final FileNotFoundException fnfe) {
            Messager.showMessage(
                    "Loading failed, because the file was not found?");
            TAP.getErrorLogger().logNonFatalError(fnfe);
            this.handleDeferredSuccess(false);
        } catch (final IOException ie) {
            Messager.showMessage(
                    "Loading failed, because a file I/O error occurred.");
            TAP.getErrorLogger().logNonFatalError(ie);
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

    @Override
    public void openFiles(final OpenFilesEvent inE) {
        this.loadAdventureFile(inE.getFiles().get(0).getAbsolutePath());
    }
}
