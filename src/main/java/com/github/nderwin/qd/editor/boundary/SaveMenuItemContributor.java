package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.spi.boundary.MenuItemContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class SaveMenuItemContributor implements MenuItemContributor {

    @Inject
    SaveAction saveAction;

    @Inject
    SaveAllAction saveAllAction;

    @Override
    public void contribute(final JMenu menu) {
        menu.add(new JMenuItem(saveAction));
        menu.add(new JMenuItem(saveAllAction));
    }

    @Override
    public int order() {
        return 300;
    }

}
