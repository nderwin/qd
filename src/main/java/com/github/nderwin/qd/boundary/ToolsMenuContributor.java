package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.spi.boundary.MenuContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class ToolsMenuContributor implements MenuContributor {

    @Inject
    I18n i18n;

    @Inject
    PreferencesAction preferencesAction;

    @Override
    public void contribute(final JMenuBar menuBar) {
        var toolsMenu = new JMenu();
        toolsMenu.setMnemonic(i18n.getMnemonic("menu.tools.mnemonic"));
        toolsMenu.setText(i18n.getMessage("menu.tools"));
        var preferencesMenuItem = new JMenuItem();
        preferencesMenuItem.setAction(preferencesAction);
        toolsMenu.add(preferencesMenuItem);

        menuBar.add(toolsMenu);
    }

    @Override
    public int order() {
        return 800;
    }

}
