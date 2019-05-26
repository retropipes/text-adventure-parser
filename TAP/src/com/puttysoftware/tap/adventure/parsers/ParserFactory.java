package com.puttysoftware.tap.adventure.parsers;

import com.puttysoftware.tap.adventure.parser1.InputParser1;

public final class ParserFactory {
    // Constants
    public static final int GRAMMAR_1 = 1;
    // Caches
    private static InputParser PARSER_1;

    private ParserFactory() {
        // Do nothing: static methods only
    }

    public static InputParser getParser(final int grammarVersion) {
        // Create parser 1 if needed
        if (ParserFactory.PARSER_1 == null) {
            ParserFactory.PARSER_1 = new InputParser1();
        }
        // Get a parser
        if (grammarVersion == ParserFactory.GRAMMAR_1) {
            return ParserFactory.PARSER_1;
        }
        // Unknown grammar version
        throw new UnsupportedOperationException("" + grammarVersion);
    }
}
