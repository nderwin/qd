package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.spi.boundary.MenuItemContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class NewMenuItemContributor implements MenuItemContributor {

    @Inject
    NewAction newAction;

    @Override
    public void contribute(final JMenu menu) {
        menu.add(new JMenuItem(newAction));
    }

    @Override
    public int order() {
        return 100;
    }

}
