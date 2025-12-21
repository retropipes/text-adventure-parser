package com.puttysoftware.tap.adventure.parsers;

class NormalAdventureMessage implements ValueBearer {
    private final String msgVal;

    private NormalAdventureMessage(final String value) {
        this.msgVal = value;
    }

    @Override
    public String value() {
        return this.msgVal;
    }

    public static NormalAdventureMessage create(final String value) {
        return new NormalAdventureMessage(value);
    }
}
