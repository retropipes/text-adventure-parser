package com.puttysoftware.tap.messages;

import com.puttysoftware.tap.adventure.parsers.ValueBearer;

public final class MessageArgument {
    // Fields
    private final String value;

    // Constructor
    private MessageArgument(final String source) {
        this.value = source;
    }

    // Methods
    public String value() {
        return this.value;
    }

    // Factories
    public static MessageArgument fromMessage(final Message msg) {
        return new MessageArgument(Messages.getMessageValue(msg));
    }

    public static MessageArgument fromInteger(final int i) {
        return new MessageArgument(Integer.toString(i));
    }

    public static MessageArgument fromValueBearer(final ValueBearer vb) {
        return new MessageArgument(vb.value());
    }
}
