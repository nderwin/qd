package com.github.nderwin.qd.spi.boundary;

import java.util.Optional;
import org.kordamp.ikonli.swing.FontIcon;

import module java.desktop;

public interface UIListener {

    Optional<FontIcon> getSmallIcon();

    Optional<FontIcon> getLargeIcon();

    default void uiRefreshed() {
        if (getSmallIcon().isPresent()) {
            getSmallIcon().get().setIconColor(UIManager.getColor("MenuItem.foreground"));
        }

        if (getLargeIcon().isPresent()) {
            getLargeIcon().get().setIconColor(UIManager.getColor("MenuItem.foreground"));
        }
    }

}
