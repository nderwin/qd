package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.ApplicationExitEvent;
import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.control.UserPreferencesStore;
import io.quarkiverse.desktop.awt.DesktopStartupEvent;
import io.quarkus.logging.Log;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class MainFrame extends JFrame {

    private static final int MIN_HEIGHT = 200;

    private static final int MIN_WIDTH = 300;

    private static final double SCREEN_PERCENTAGE_DEFAULT = 0.45;

    private static final int MAXIMIZATION_THRESHOLD = 8;

    @Inject
    I18n i18n;

    @Inject
    IconManager iconManager;

    @Inject
    UserPreferencesStore prefStore;

    @Inject
    AboutAction aboutAction;

    @Inject
    ExitAction exitAction;

    private JMenuItem aboutMenuItem;

    private JMenuItem exitMenuItem;

    private JMenu fileMenu;

    private JMenu helpMenu;

    private JMenuBar menuBar;

    /**
     * Last trusted, non-maximized window geometry (what gets persisted).
     */
    private WindowGeometry geometry;

    /**
     * Latest component geometry pending confirmation. Never persisted directly.
     */
    private WindowGeometry candidateGeometry;

    private volatile int trackedState = Frame.NORMAL;

    private Timer geometryTimer;

    private volatile boolean geometryInitialized;

    void open(@Observes final DesktopStartupEvent evt) {
        initComponents();

        var config = getGraphicsConfiguration();
        var bounds = config.getBounds();

        int targetWidth = (int) Math.floor(bounds.width * SCREEN_PERCENTAGE_DEFAULT);
        int targetHeight = (int) Math.floor(bounds.height * SCREEN_PERCENTAGE_DEFAULT);

        int finalWidth = Math.max(getMinimumSize().width, targetWidth);
        int finalHeight = Math.max(getMinimumSize().height, targetHeight);

        setPreferredSize(new Dimension(finalWidth, finalHeight));

        pack();

        var restored = prefStore.loadWindowPersistence();
        if (restored.isPresent() && isOnScreen(restored.get().restoredBounds())) {
            var restoredGeometry = fitToScreen(clampToMinimum(restored.get().restoredBounds()));
            geometry = restoredGeometry;
            trackedState = restored.get().extendedState();

            setLocationByPlatform(false);
            setBounds(
                    restoredGeometry.x(),
                    restoredGeometry.y(),
                    restoredGeometry.w(),
                    restoredGeometry.h()
            );

            if (Frame.NORMAL != trackedState) {
                setExtendedState(trackedState);
            }

            Log.debug("Restored window geometry: " + geometry + " state=" + trackedState);
        } else {
            setLocationByPlatform(true);
        }

        geometryTimer = new Timer(250, e -> persistPreferences());
        geometryTimer.setRepeats(false);
        geometryTimer.setCoalesce(true);

        installWindowListeners();

        setVisible(true);
    }

    void onExit(@Observes final ApplicationExitEvent event) {
        flushPreferences();
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void installWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(final WindowEvent e) {
                geometryInitialized = true;

                if (null == geometry && Frame.NORMAL == trackedState && !looksMaximized()) {
                    geometry = new WindowGeometry(getX(), getY(), getWidth(), getHeight());
                    Log.debug("Captured initial normal geometry: " + geometry);
                }
            }

            @Override
            public void windowClosing(final WindowEvent e) {
                flushPreferences();
            }
        });

        addWindowStateListener(wsl -> {
            int oldState = wsl.getOldState();
            int newState = wsl.getNewState();

            if (Frame.NORMAL == oldState && Frame.NORMAL != newState) {
                // Preserve the last trusted (non-maximized) geometry instead of
                // capturing the possibly-already-maximized current bounds.
                if (null == geometry && null != candidateGeometry && !isScreenSized(candidateGeometry)) {
                    geometry = candidateGeometry;
                }
                if (null == geometry) {
                    geometry = new WindowGeometry(getX(), getY(), getWidth(), getHeight());
                    Log.debug("Fallback captured geometry on state change: " + geometry);
                } else {
                    Log.debug("Preserving normal geometry during state change: " + geometry);
                }

                candidateGeometry = null;

                if (null != geometryTimer && geometryTimer.isRunning()) {
                    geometryTimer.stop();
                }
            } else if (Frame.NORMAL != oldState && Frame.NORMAL == newState) {
                WindowGeometry restoredBounds = new WindowGeometry(getX(), getY(), getWidth(), getHeight());
                if (!isScreenSized(restoredBounds)) {
                    geometry = restoredBounds;
                }
                Log.debug("Returned to normal: " + geometry);
            }

            trackedState = newState;
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(final ComponentEvent e) {
                updateGeometry();
            }

            @Override
            public void componentResized(final ComponentEvent e) {
                updateGeometry();
            }
        });
    }

    private void updateGeometry() {
        if (!geometryInitialized || Frame.NORMAL != trackedState || looksMaximized()) {
            return;
        }

        if (0 == getX() && 0 == getY() && getWidth() <= 0) {
            return;
        }

        // Only record as a pending candidate; promotion happens later in
        // persistPreferences(), and only if the state is still normal.
        candidateGeometry = new WindowGeometry(getX(), getY(), getWidth(), getHeight());
        Log.debug("Updated candidate geometry: " + candidateGeometry);

        geometryTimer.restart();
    }

    private void persistPreferences() {
        // Give up on any candidate if the state has changed meanwhile.
        if (Frame.NORMAL != trackedState) {
            candidateGeometry = null;
            Log.debug("Skipping geometry update: state=" + trackedState);
            // Still persist the last trusted geometry + current state.
            if (null != geometry) {
                prefStore.saveWindowPersistence(new WindowPersistence(geometry, trackedState));
            }
            return;
        }

        if (null != candidateGeometry && !isScreenSized(candidateGeometry)) {
            geometry = candidateGeometry;
            candidateGeometry = null;
            Log.debug("Promoted candidate geometry: " + geometry);
        } else {
            candidateGeometry = null;
        }

        if (null == geometry) {
            return;
        }

        Log.debug("Persisting: " + geometry + " - " + trackedState);
        prefStore.saveWindowPersistence(new WindowPersistence(geometry, trackedState));
    }

    private void flushPreferences() {
        if (null != geometryTimer && geometryTimer.isRunning()) {
            geometryTimer.stop();
        }
        persistPreferences();
    }

    private boolean looksMaximized() {
        var gc = getGraphicsConfiguration();

        if (null == gc) {
            return false;
        }

        var screen = gc.getBounds();

        return getWidth() >= screen.width - MAXIMIZATION_THRESHOLD && getHeight() >= screen.height - MAXIMIZATION_THRESHOLD;
    }

    private boolean isScreenSized(final WindowGeometry g) {
        var gc = getGraphicsConfiguration();

        if (null == gc) {
            return false;
        }

        var screen = gc.getBounds();

        return g.w() >= screen.width - MAXIMIZATION_THRESHOLD || g.h() >= screen.height - MAXIMIZATION_THRESHOLD;
    }

    private static WindowGeometry clampToMinimum(final WindowGeometry g) {
        return new WindowGeometry(
                g.x(),
                g.y(),
                Math.max(g.w(), MIN_WIDTH),
                Math.max(g.h(), MIN_HEIGHT)
        );
    }

    private static boolean isOnScreen(final WindowGeometry g) {
        var env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var windowBounds = new Rectangle(g.x(), g.y(), g.w(), g.h());

        for (var device : env.getScreenDevices()) {
            var screenBounds = device.getDefaultConfiguration().getBounds();
            if (screenBounds.intersects(windowBounds)) {
                return true;
            }
        }
        return false;
    }

    private static WindowGeometry fitToScreen(final WindowGeometry g) {
        var env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var windowBounds = new Rectangle(g.x(), g.y(), g.w(), g.h());

        GraphicsDevice bestDevice = null;
        Rectangle bestIntersection = new Rectangle();
        for (var device : env.getScreenDevices()) {
            var screenBounds = device.getDefaultConfiguration().getBounds();
            var intersection = screenBounds.intersection(windowBounds);
            if (intersection.width * intersection.height > bestIntersection.width * bestIntersection.height) {
                bestIntersection = intersection;
                bestDevice = device;
            }
        }

        if (null == bestDevice) {
            return g;
        }

        var config = bestDevice.getDefaultConfiguration();
        var screen = config.getBounds();
        var insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

        int usableX = screen.x + insets.left;
        int usableY = screen.y + insets.top;
        int usableW = screen.width - insets.left - insets.right;
        int usableH = screen.height - insets.top - insets.bottom;

        int w = Math.min(g.w(), Math.max(MIN_WIDTH, usableW));
        int h = Math.min(g.h(), Math.max(MIN_HEIGHT, usableH));

        int x = g.x();
        int y = g.y();

        if (x + w > usableX + usableW) {
            x = usableX + usableW - w;
        }
        if (x < usableX) {
            x = usableX;
        }
        if (y + h > usableY + usableH) {
            y = usableY + usableH - h;
        }
        if (y < usableY) {
            y = usableY;
        }

        return new WindowGeometry(x, y, w, h);
    }

    private void initComponents() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        exitMenuItem = new JMenuItem();
        helpMenu = new JMenu();
        aboutMenuItem = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(i18n.getMessage("app.title"));
        setIconImage(iconManager.getAppIcon().getImage());
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        fileMenu.setMnemonic(i18n.getMnemonic("menu.file.mnemonic"));
        fileMenu.setText(i18n.getMessage("menu.file"));

        exitMenuItem.setAction(exitAction);
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setMnemonic(i18n.getMnemonic("menu.help.mnemonic"));
        helpMenu.setText(i18n.getMessage("menu.help"));

        aboutMenuItem.setAction(aboutAction);
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

}
