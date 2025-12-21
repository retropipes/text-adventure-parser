package com.puttysoftware.tap.adventure.parsers;

public final class ParserFactory {
    // Constants
    public static final int GRAMMAR_0 = 0;
    // Caches
    private static NormalInputParser PARSER_0;

    private ParserFactory() {
        // Do nothing: static methods only
    }

    public static InputParser getParser(final int grammarVersion) {
        // Create parser 0 if needed
        if (ParserFactory.PARSER_0 == null) {
            ParserFactory.PARSER_0 = new NormalInputParser();
        }
        // Get a parser
        if (grammarVersion == ParserFactory.GRAMMAR_0) {
            return ParserFactory.PARSER_0;
        }
        // Unknown grammar version
        throw new UnsupportedOperationException("" + grammarVersion); //$NON-NLS-1$
    }
}
