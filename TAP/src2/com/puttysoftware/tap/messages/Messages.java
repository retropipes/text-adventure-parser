package com.puttysoftware.tap.messages;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "strings.bundle"; //$NON-NLS-1$
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(Messages.BUNDLE_NAME);

    private Messages() {
    }

    private static String getMessageValue(final Message key) {
        try {
            return Messages.RESOURCE_BUNDLE
                    .getString(Integer.toString(key.ordinal()));
        } catch (final MissingResourceException e) {
            return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public static String getMessageValue(final Message msg,
            final MessageArgument... args) {
        String value = Messages.getMessageValue(msg);
        int m = 0;
        for (final MessageArgument arg : args) {
            value = value.replace("%" + m, arg.value()); //$NON-NLS-1$
            m++;
        }
        return value;
    }
}
