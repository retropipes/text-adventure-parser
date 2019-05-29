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
    }

    protected void loadExampleAdventure(final ArrayList<String> data) {
        this.advData = data;
        this.createParser();
    }

    private void createParser() {
        this.parser = ParserFactory.getParser(ParserFactory.GRAMMAR_0);
        this.parser.doInitial(this.advData);
    }

    private void loadData(final File advFile) throws IOException {
        try (final FileInputStream fis = new FileInputStream(advFile);
                final ResourceStreamReader rsr = new ResourceStreamReader(
                        fis)) {
            this.advData = new ArrayList<>();
            String line = "";
            while (line != null) {
                line = rsr.readString();
                if (line != null) {
                    this.advData.add(line);
                }
            }
        }
    }

    public void parseCommand(final String command) {
        this.parser.parseCommand(command);
    }
}