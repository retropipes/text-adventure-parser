package com.puttysoftware.tap.adventure.parser1;

class CompoundCommands {
    // Fields
    private static final String[] COMPOUND_COMMAND_WORDS = { " and ",
            " then " };

    // Private constructor
    private CompoundCommands() {
        // Do nothing
    }

    // Methods
    protected static String[] splitCompoundInput(final String in) {
        String fixed = in;
        for (final String element : CompoundCommands.COMPOUND_COMMAND_WORDS) {
            fixed = fixed.replace(element, Commands.CHAIN_COMMAND_DELIM);
        }
        final String[] split = fixed.split(Commands.CHAIN_COMMAND_DELIM);
        return split;
    }
}
