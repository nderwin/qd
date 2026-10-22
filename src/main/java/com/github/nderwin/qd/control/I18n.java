package com.github.nderwin.qd.control;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import module java.desktop;

@ApplicationScoped
public class I18n {

    private volatile ResourceBundle bundle;

    public I18n() {
        updateBundle();
    }

    public void updateBundle() {
        this.bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
    }

    public String getMessage(final String key) {
        if (null == key) {
            return "";
        }

        try {
            return bundle.getString(key);
        } catch (final MissingResourceException _) {
            Log.warn("Missing resource key: " + key);
            return "";
        }
    }

    public int getMnemonic(final String key) {
        try {
            return KeyEvent.getExtendedKeyCodeForChar(getMessage(key).charAt(0));
        } catch (StringIndexOutOfBoundsException _) {
            return KeyEvent.VK_UNDEFINED;
        }
    }

    public KeyStroke getAccelerator(final String key) {
        return KeyStroke.getKeyStroke(getMessage(key));
    }

}
