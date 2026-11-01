package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.spi.boundary.MenuContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class HelpMenuContributor implements MenuContributor {

    @Inject
    I18n i18n;

    @Inject
    AboutAction aboutAction;

    @Override
    public void contribute(final JMenuBar menuBar) {
        var helpMenu = new JMenu();
        helpMenu.setMnemonic(i18n.getMnemonic("menu.help.mnemonic"));
        helpMenu.setText(i18n.getMessage("menu.help"));
        var aboutMenuItem = new JMenuItem();
        aboutMenuItem.setAction(aboutAction);
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);
    }

    @Override
    public int order() {
        return 1000;
    }

}
