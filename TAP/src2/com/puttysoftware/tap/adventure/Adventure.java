/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.adventure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.tap.adventure.parsers.InputParser;
import com.puttysoftware.tap.adventure.parsers.ParserFactory;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Adventure {
    // Fields
    private ArrayList<String> advData;
    private InputParser parser;

    // Constructor
    public Adventure() {
        super();
    }

    // Methods
    protected void loadAdventure(final File advFile) throws IOException {
        this.loadData(advFile);
        this.createParser();
        this.parser.doInitial(this.advData);
    }

    protected void loadExampleAdventure(final ArrayList<String> data) {
        this.advData = data;
        this.createParser();
        this.parser.doInitial(this.advData);
    }

    void loadState(final XDataReader xdr) throws IOException {
        this.createParser();
        this.advData = this.parser.loadState(xdr);
    }

    void saveState(final XDataWriter xdw) throws IOException {
        this.parser.saveState(xdw);
    }

    private void createParser() {
        this.parser = ParserFactory.getParser(ParserFactory.GRAMMAR_0);
    }

    private void loadData(final File advFile) throws IOException {
        try (final FileInputStream fis = new FileInputStream(advFile);
                final ResourceStreamReader rsr = new ResourceStreamReader(
                        fis)) {
            this.advData = new ArrayList<>();
            String line = ""; //$NON-NLS-1$
            while (line != null) {
                line = rsr.readString();
                if (line != null) {
                    this.advData.add(line);
                }
            }
        }
    }

    public void resume() {
        this.parser.doResume();
    }

    public void parseCommand(final String command) {
        this.parser.parseCommand(command);
    }
}