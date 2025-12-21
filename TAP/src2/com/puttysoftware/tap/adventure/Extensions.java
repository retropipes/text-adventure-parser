/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.adventure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.puttysoftware.tap.TAP;

class Extensions {
    // Constants
    private static Properties DATA;
    private static final int ADVENTURE = 0;
    private static final int SAVED_ADVENTURE = 1;
    static final String SEPARATOR = "."; //$NON-NLS-1$

    // Methods
    private static void loadDataIfNeeded() {
        if (Extensions.DATA == null) {
            Extensions.DATA = new Properties();
            try (final InputStream stream = Extensions.class
                    .getResourceAsStream("/assets/extensions.properties")) { //$NON-NLS-1$
                Extensions.DATA.load(stream);
            } catch (final IOException e) {
                TAP.handleException(e);
            }
        }
    }

    static String getAdventureExtension() {
        Extensions.loadDataIfNeeded();
        return Extensions.DATA
                .getProperty(Integer.toString(Extensions.ADVENTURE));
    }

    static String getSavedAdventureExtension() {
        Extensions.loadDataIfNeeded();
        return Extensions.DATA
                .getProperty(Integer.toString(Extensions.SAVED_ADVENTURE));
    }
}
