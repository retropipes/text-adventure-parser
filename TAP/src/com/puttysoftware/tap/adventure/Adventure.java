/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.adventure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.tap.adventure.parsers.InputParser;
import com.puttysoftware.tap.adventure.parsers.ParserFactory;

public class Adventure {
    // Fields
    private final ArrayList<String> advData;
    private InputParser parser;

    // Constructor
    public Adventure() {
        this.advData = new ArrayList<>();
    }

    // Methods
    protected void loadAdventure(final File advFile) throws IOException {
        this.loadData(advFile);
        this.parser = ParserFactory.getParser(ParserFactory.GRAMMAR_1);
        this.parser.doInitial(this.advData);
    }

    private void loadData(final File advFile) throws IOException {
        try (final FileInputStream fis = new FileInputStream(advFile);
                final ResourceStreamReader rsr = new ResourceStreamReader(
                        fis)) {
            String line = "";
            while (line != null) {
                line = rsr.readString();
                if (line != null) {
                    this.advData.add(line);
                }
            }
            rsr.close();
        }
    }

    public void parseCommand(final String command) {
        this.parser.parseCommand(command);
    }
}