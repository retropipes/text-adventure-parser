package com.puttysoftware.tap.adventure.parsers;

@SuppressWarnings("nls")
class InputStripper {
    // Fields
    private static final String[] STRIP_BEGIN_WORDS = { "go ", "travel ", "[" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    private static final String[] STRIP_MIDDLE_WORDS = { " a ", " an ", " the ", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            " to ", "=", ", " }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    private static final String[] STRIP_END_WORDS = { ".", "?", "!", "]" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    // Private constructor
    private InputStripper() {
        // Do nothing
    }

    // Methods
    public static String stripInput(final String in) {
        // Case normalization
        String out = in.toLowerCase();
        // Begin stripping
        for (final String element : InputStripper.STRIP_BEGIN_WORDS) {
            if (out.startsWith(element)) {
                out = out.replace(element, ""); //$NON-NLS-1$
            }
        }
        // Middle stripping
        for (final String element : InputStripper.STRIP_MIDDLE_WORDS) {
            out = out.replace(element, " "); //$NON-NLS-1$
        }
        // End stripping
        for (final String element : InputStripper.STRIP_END_WORDS) {
            if (out.endsWith(element)) {
                out = out.replace(element, ""); //$NON-NLS-1$
            }
        }
        // Multiple space stripping
        while (out.contains("  ")) { //$NON-NLS-1$
            out = out.replace("  ", " "); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Begin and end space stripping
        return out.trim();
    }
}
