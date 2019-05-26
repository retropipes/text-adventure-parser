package com.puttysoftware.tap.adventure.parser1;

class InputStripper {
    // Fields
    private static final String[] STRIP_BEGIN_WORDS = { "go ", "travel ", "[" };
    private static final String[] STRIP_MIDDLE_WORDS = { " a ", " an ", " the ",
            " to ", "=", ", " };
    private static final String[] STRIP_END_WORDS = { ".", "?", "!", "]" };

    // Private constructor
    private InputStripper() {
        // Do nothing
    }

    // Methods
    protected static String stripInput(final String in) {
        // Case normalization
        String out = in.toLowerCase();
        // Begin stripping
        for (final String element : InputStripper.STRIP_BEGIN_WORDS) {
            if (out.startsWith(element)) {
                out = out.replace(element, "");
            }
        }
        // Middle stripping
        for (final String element : InputStripper.STRIP_MIDDLE_WORDS) {
            out = out.replace(element, " ");
        }
        // End stripping
        for (final String element : InputStripper.STRIP_END_WORDS) {
            if (out.endsWith(element)) {
                out = out.replace(element, "");
            }
        }
        // Multiple space stripping
        while (out.contains("  ")) {
            out = out.replace("  ", " ");
        }
        // Begin and end space stripping
        return out.trim();
    }
}
