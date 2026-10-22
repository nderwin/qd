package com.github.nderwin.qd.control;

import com.github.nderwin.qd.boundary.WindowGeometry;
import com.github.nderwin.qd.boundary.WindowPersistence;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static com.github.nderwin.qd.boundary.WindowGeometry.DEFAULT;
import static java.awt.Frame.NORMAL;

@ApplicationScoped
public class UserPreferencesStore {

    private static final String KEY_X = "window.main.x";

    private static final String KEY_Y = "window.main.y";

    private static final String KEY_W = "window.main.w";

    private static final String KEY_H = "window.main.h";

    private static final String KEY_STATE = "window.main.state";

    private static final String KEY_INIT = "window.main.init";

    private final Preferences node;

    public UserPreferencesStore() {
        this.node = Preferences.userNodeForPackage(UserPreferencesStore.class);
    }

    public Optional<WindowPersistence> loadWindowPersistence() {
        if (!node.getBoolean(KEY_INIT, false)) {
            return Optional.empty();
        }

        var geo = new WindowGeometry(
                node.getInt(KEY_X, DEFAULT.x()),
                node.getInt(KEY_Y, DEFAULT.y()),
                node.getInt(KEY_W, DEFAULT.w()),
                node.getInt(KEY_H, DEFAULT.h())
        );

        int state = node.getInt(KEY_STATE, NORMAL);

        return Optional.of(new WindowPersistence(geo, state));
    }

    public void saveWindowPersistence(final WindowPersistence wp) {
        var geo = wp.restoredBounds();

        node.putInt(KEY_X, geo.x());
        node.putInt(KEY_Y, geo.y());
        node.putInt(KEY_W, geo.w());
        node.putInt(KEY_H, geo.h());

        node.putInt(KEY_STATE, wp.extendedState());
        node.putBoolean(KEY_INIT, true);

        try {
            node.flush();
        } catch (final BackingStoreException ex) {
            Log.warn("Failed to persist window geometry to OS preferences", ex);
        }
    }

}
