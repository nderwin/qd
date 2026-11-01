package com.github.nderwin.qd.boundary;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.github.nderwin.qd.spi.boundary.UIListener;
import io.quarkus.logging.Log;
import jakarta.enterprise.inject.spi.CDI;
import java.util.Arrays;

import module java.desktop;

public interface LafSupport {

    String DEFAULT_LAF = FlatLightLaf.class.getName();

    static void installFlatLookAndFeels() {
        FlatLaf.setPreferredFontFamily("Noto Sans");

        FlatLightLaf.installLafInfo();
        FlatDarkLaf.installLafInfo();
        FlatDarculaLaf.installLafInfo();
        FlatIntelliJLaf.installLafInfo();
        FlatMacLightLaf.installLafInfo();
        FlatMacDarkLaf.installLafInfo();
    }

    static UIManager.LookAndFeelInfo[] installed() {
        Log.debug("L&Fs: " + Arrays.toString(UIManager.getInstalledLookAndFeels()));
        return UIManager.getInstalledLookAndFeels();
    }

    private static void restoreToolbarFloat(final Container container) {
        if (null == container) {
            return;
        }

        for (var component : container.getComponents()) {
            if (component instanceof final JToolBar bar) {
                bar.setFloatable(true);
            }

            if (component instanceof final Container child) {
                restoreToolbarFloat(child);
            }
        }
    }

    static void apply(final String lafClassName) {
        var appliedLaf = (null == lafClassName || lafClassName.isBlank() ? DEFAULT_LAF : lafClassName);
        var applied = false;

        try {
            UIManager.setLookAndFeel(appliedLaf);
            applied = true;
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Log.warn("Failed to set look and feel: " + appliedLaf, ex);

            try {
                appliedLaf = DEFAULT_LAF;

                UIManager.setLookAndFeel(appliedLaf);
                applied = true;
            } catch (final Exception e) {
                Log.warn("Failed to fall back to default look and feel: " + appliedLaf, e);
            }
        }

        if (applied) {
            if (appliedLaf.startsWith("com.formdev.flatlaf")) {
                FlatLaf.updateUI();
            } else {
                for (var window : Window.getWindows()) {
                    SwingUtilities.updateComponentTreeUI(window);
                    restoreToolbarFloat(window);
                    window.validate();
                    window.repaint();
                }

            }

            CDI.current().select(UIListener.class).stream()
                    .forEach(l -> l.uiRefreshed());
        }
    }
}
