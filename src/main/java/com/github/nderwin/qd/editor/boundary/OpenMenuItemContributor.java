package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.spi.boundary.MenuItemContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class OpenMenuItemContributor implements MenuItemContributor {

    @Inject
    OpenAction openAction;

    @Override
    public void contribute(final JMenu menu) {
        menu.add(new JMenuItem(openAction));
    }

    @Override
    public int order() {
        return 200;
    }

}
