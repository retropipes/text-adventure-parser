/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.adventure;

import java.awt.FileDialog;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.random.RandomHex;
import com.puttysoftware.sandbox.Sandbox;
import com.puttysoftware.tap.TAP;
import com.puttysoftware.tap.game.Game;
import com.puttysoftware.tap.messages.Message;
import com.puttysoftware.tap.messages.Messages;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class AdventureManager implements OpenFilesHandler {
    // Fields
    private Adventure currentAdventure;
    private String currentSavedAdventureDisplay;
    private String currentSavedAdventureFilename;
    private boolean loaded;
    private String[] exampleFiles;
    private String[] exampleDisplays;
    private ArrayList<String> saveFiles;
    private ArrayList<String> saveDisplays;
    private static final int FILENAME_RANDOMNESS = 32;
    private static final String EXAMPLE_LIST_FILE = "/assets/examples.files.txt"; //$NON-NLS-1$
    private static final String EXAMPLE_DISPLAY_LIST_FILE = "/assets/examples.display.txt"; //$NON-NLS-1$
    private static final String SAVE_LIST_FILE = "save.files.txt"; //$NON-NLS-1$
    private static final String SAVE_DISPLAY_LIST_FILE = "save.display.txt"; //$NON-NLS-1$
    private static final String SAVE_DOC_TAG = "saved-adventure"; //$NON-NLS-1$

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

    public boolean isLoaded() {
        return this.loaded;
    }

    private void setOpened() {
        this.loaded = true;
        TAP.getGame().onOpened();
    }

    public void closeAdventure() {
        this.currentAdventure = null;
        this.currentSavedAdventureDisplay = null;
        this.currentSavedAdventureFilename = null;
        this.loaded = false;
        TAP.getGame().onClosed();
    }

    public void loadExampleAdventure() {
        try {
            this.loadLists();
            final String choice = CommonDialogs.showInputDialog(
                    Messages.getMessageValue(
                            Message.TITLE_WHICH_EXAMPLE_ADVENTURE),
                    Messages.getMessageValue(
                            Message.TITLE_LOAD_EXAMPLE_ADVENTURE),
                    this.exampleDisplays, this.exampleDisplays[0]);
            final int index = this.getAdventureIndex(choice);
            if (index != -1) {
                final String exName = "/examples/" + this.exampleFiles[index] //$NON-NLS-1$
                        + Extensions.SEPARATOR
                        + Extensions.getAdventureExtension();
                final ArrayList<String> data = new ArrayList<>();
                try (InputStream is = AdventureManager.class
                        .getResourceAsStream(exName);
                        ResourceStreamReader ris = new ResourceStreamReader(
                                is)) {
                    String line = ""; //$NON-NLS-1$
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
            TAP.getGame().showMessage(Message.ERROR_LOAD_FAIL);
            TAP.handleException(ioe);
        }
    }

    public void loadSavedAdventure() {
        try {
            this.loadLists();
            if (this.saveFiles.size() > 0 && this.saveDisplays.size() > 0) {
                final String choice = CommonDialogs.showInputDialog(
                        Messages.getMessageValue(
                                Message.TITLE_WHICH_SAVED_ADVENTURE),
                        Messages.getMessageValue(
                                Message.TITLE_LOAD_SAVED_ADVENTURE),
                        this.saveDisplays
                                .toArray(new String[this.saveDisplays.size()]),
                        this.saveDisplays.get(0));
                final int index = this.getSaveIndex(choice);
                if (index != -1) {
                    final File saveData = new File(
                            Sandbox.getSandbox().getDocumentsDirectory()
                                    + File.pathSeparator
                                    + this.saveFiles.get(index)
                                    + Extensions.SEPARATOR
                                    + Extensions.getSavedAdventureExtension());
                    if (saveData.exists() && saveData.canRead()) {
                        try (FileInputStream is = new FileInputStream(saveData);
                                XDataReader xdr = new XDataReader(is,
                                        AdventureManager.SAVE_DOC_TAG)) {
                            this.loadSavedAdventureData(xdr);
                            this.getAdventure().resume();
                        }
                    }
                }
            } else {
                CommonDialogs.showDialog(
                        Messages.getMessageValue(Message.ERROR_NO_SAVES));
            }
        } catch (final IOException ioe) {
            TAP.getGame().showMessage(Message.ERROR_LOAD_FAIL);
            TAP.handleException(ioe);
        }
    }

    public void saveAdventure() {
        if (this.currentSavedAdventureFilename == null
                || this.currentSavedAdventureDisplay == null) {
            this.saveAdventureAs();
        } else {
            try {
                final int index = this
                        .getSaveIndex(this.currentSavedAdventureDisplay);
                if (index != -1) {
                    if (this.saveFiles
                            .get(index) != this.currentSavedAdventureFilename) {
                        this.currentSavedAdventureFilename = this.saveFiles
                                .get(index);
                    }
                    final String saveName = Sandbox.getSandbox()
                            .getDocumentsDirectory() + File.pathSeparator
                            + this.currentSavedAdventureFilename
                            + Extensions.SEPARATOR
                            + Extensions.getSavedAdventureExtension();
                    final File saveData = new File(saveName);
                    if (!saveData.exists()) {
                        try (XDataWriter xdw = new XDataWriter(saveName,
                                AdventureManager.SAVE_DOC_TAG)) {
                            this.saveAdventureData(xdw);
                        }
                    }
                }
            } catch (final IOException ioe) {
                TAP.getGame().showMessage(Message.ERROR_SAVE_FAIL);
                TAP.handleException(ioe);
            }
        }
    }

    public void saveAdventureAs() {
        try {
            this.loadLists();
            final String choice = CommonDialogs.showTextInputDialog(
                    Messages.getMessageValue(Message.TITLE_SAVE_NAME),
                    Messages.getMessageValue(Message.TITLE_SAVE_ADVENTURE));
            int index = -1;
            if (this.saveFiles.size() > 0 && this.saveDisplays.size() > 0) {
                index = this.getSaveIndex(choice);
            }
            this.currentSavedAdventureDisplay = choice;
            if (index == -1) {
                this.currentSavedAdventureFilename = RandomHex
                        .nextN(AdventureManager.FILENAME_RANDOMNESS);
                final String saveName = Sandbox.getSandbox()
                        .getDocumentsDirectory() + File.pathSeparator
                        + this.currentSavedAdventureFilename
                        + Extensions.SEPARATOR
                        + Extensions.getSavedAdventureExtension();
                final File saveData = new File(saveName);
                if (!saveData.exists()) {
                    try (XDataWriter xdw = new XDataWriter(saveName,
                            AdventureManager.SAVE_DOC_TAG)) {
                        this.saveAdventureData(xdw);
                    }
                }
                this.saveFiles.add(this.currentSavedAdventureFilename);
                this.saveDisplays.add(this.currentSavedAdventureDisplay);
                this.saveLists();
            } else {
                this.currentSavedAdventureFilename = this.saveFiles.get(index);
                this.saveAdventure();
            }
        } catch (final IOException ioe) {
            TAP.getGame().showMessage(Message.ERROR_SAVE_FAIL);
            TAP.handleException(ioe);
        }
    }

    private int getAdventureIndex(final String choice) {
        if (choice != null && !choice.isEmpty()) {
            for (int i = 0; i < this.exampleDisplays.length; i++) {
                if (this.exampleDisplays[i].equals(choice)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getSaveIndex(final String choice) {
        if (choice != null && !choice.isEmpty()) {
            final int length = this.saveDisplays.size();
            for (int i = 0; i < length; i++) {
                if (this.saveDisplays.get(i).equals(choice)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void loadLists() throws IOException {
        if (this.exampleFiles == null) {
            final ArrayList<String> exampleFilesAL = new ArrayList<>();
            try (InputStream is = AdventureManager.class
                    .getResourceAsStream(AdventureManager.EXAMPLE_LIST_FILE);
                    ResourceStreamReader ris = new ResourceStreamReader(is)) {
                String line = ""; //$NON-NLS-1$
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
        if (this.exampleDisplays == null) {
            final ArrayList<String> exampleDisplaysAL = new ArrayList<>();
            try (InputStream is = AdventureManager.class.getResourceAsStream(
                    AdventureManager.EXAMPLE_DISPLAY_LIST_FILE);
                    ResourceStreamReader ris = new ResourceStreamReader(is)) {
                String line = ""; //$NON-NLS-1$
                while (line != null) {
                    line = ris.readString();
                    if (line != null) {
                        exampleDisplaysAL.add(line);
                    }
                }
            }
            this.exampleDisplays = exampleDisplaysAL
                    .toArray(new String[exampleDisplaysAL.size()]);
        }
        if (this.saveFiles == null) {
            final ArrayList<String> saveFilesAL = new ArrayList<>();
            final File saveData = new File(
                    Sandbox.getSandbox().getDocumentsDirectory()
                            + File.pathSeparator
                            + AdventureManager.SAVE_LIST_FILE);
            if (saveData.exists() && saveData.canRead()) {
                try (FileInputStream is = new FileInputStream(saveData);
                        ResourceStreamReader ris = new ResourceStreamReader(
                                is)) {
                    String line = ""; //$NON-NLS-1$
                    while (line != null) {
                        line = ris.readString();
                        if (line != null) {
                            saveFilesAL.add(line);
                        }
                    }
                }
            }
            this.saveFiles = saveFilesAL;
        }
        if (this.saveDisplays == null) {
            final ArrayList<String> saveDisplaysAL = new ArrayList<>();
            final File saveDisplayData = new File(
                    Sandbox.getSandbox().getDocumentsDirectory()
                            + File.pathSeparator
                            + AdventureManager.SAVE_DISPLAY_LIST_FILE);
            if (saveDisplayData.exists() && saveDisplayData.canRead()) {
                try (FileInputStream is = new FileInputStream(saveDisplayData);
                        ResourceStreamReader ris = new ResourceStreamReader(
                                is)) {
                    String line = ""; //$NON-NLS-1$
                    while (line != null) {
                        line = ris.readString();
                        if (line != null) {
                            saveDisplaysAL.add(line);
                        }
                    }
                }
            }
            this.saveDisplays = saveDisplaysAL;
        }
    }

    private void saveLists() throws IOException {
        if (this.saveFiles != null && this.saveDisplays != null) {
            final File saveData = new File(
                    Sandbox.getSandbox().getDocumentsDirectory()
                            + File.pathSeparator
                            + AdventureManager.SAVE_LIST_FILE);
            if (saveData.exists() && saveData.canWrite()
                    || !saveData.exists()) {
                try (FileWriter fw = new FileWriter(saveData);
                        PrintWriter pw = new PrintWriter(fw)) {
                    for (final String line : this.saveFiles) {
                        pw.println(line);
                    }
                }
            }
        }
        final File saveDisplayData = new File(
                Sandbox.getSandbox().getDocumentsDirectory()
                        + File.pathSeparator
                        + AdventureManager.SAVE_DISPLAY_LIST_FILE);
        if (saveDisplayData.exists() && saveDisplayData.canWrite()
                || !saveDisplayData.exists()) {
            try (FileWriter fw = new FileWriter(saveDisplayData);
                    PrintWriter pw = new PrintWriter(fw)) {
                for (final String line : this.saveDisplays) {
                    pw.println(line);
                }
            }
        }
    }

    private void loadExampleData(final ArrayList<String> adventureData) {
        final Game game = TAP.getGame();
        final Adventure gameAdventure = new Adventure();
        game.clearOutput();
        gameAdventure.loadExampleAdventure(adventureData);
        this.setAdventure(gameAdventure);
        this.setOpened();
    }

    public void loadAdventure() {
        final Game game = TAP.getGame();
        String dirname, filename, extension;
        final FileDialog fd = new FileDialog(game.getOutputFrame(),
                Messages.getMessageValue(Message.TITLE_LOAD_ADVENTURE),
                FileDialog.LOAD);
        fd.setVisible(true);
        filename = fd.getFile();
        dirname = fd.getDirectory();
        if (filename != null) {
            final File file = new File(dirname + filename);
            extension = AdventureManager.getExtension(file);
            if (extension.equals(Extensions.getAdventureExtension())) {
                this.loadAdventureData(dirname + filename);
            } else {
                game.showMessage(Message.ERROR_NOT_AN_ADVENTURE);
            }
        }
    }

    private void loadAdventureData(final String filename) {
        final Game game = TAP.getGame();
        try {
            final File adventureFile = new File(filename);
            final Adventure gameAdventure = new Adventure();
            game.clearOutput();
            gameAdventure.loadAdventure(adventureFile);
            this.setAdventure(gameAdventure);
            this.setOpened();
        } catch (final IOException ie) {
            game.showMessage(Message.ERROR_LOAD_FAIL);
            TAP.handleException(ie);
            this.closeAdventure();
        } catch (final Exception ex) {
            TAP.handleException(ex);
        }
    }

    private void loadSavedAdventureData(final XDataReader xdr)
            throws IOException {
        final Game game = TAP.getGame();
        final Adventure gameAdventure = new Adventure();
        game.clearOutput();
        gameAdventure.loadState(xdr);
        this.setAdventure(gameAdventure);
        this.setOpened();
    }

    private void saveAdventureData(final XDataWriter xdw) throws IOException {
        this.getAdventure().saveState(xdw);
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
        this.loadAdventureData(inE.getFiles().get(0).getAbsolutePath());
    }
}
