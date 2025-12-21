package com.puttysoftware.tap.adventure.parsers;

class CompoundNormalCommands {
    // Fields
    private static final String[] COMPOUND_COMMAND_WORDS = { " and ", //$NON-NLS-1$
            " then " }; //$NON-NLS-1$

    // Private constructor
    private CompoundNormalCommands() {
        // Do nothing
    }

    // Methods
    protected static String[] splitCompoundInput(final String in) {
        String fixed = in;
        for (final String element : CompoundNormalCommands.COMPOUND_COMMAND_WORDS) {
            fixed = fixed.replace(element,
                    NormalCommand.CHAIN_COMMAND_DELIM.value());
        }
        final String[] split = fixed
                .split(NormalCommand.CHAIN_COMMAND_DELIM.value());
        return split;
    }
}
