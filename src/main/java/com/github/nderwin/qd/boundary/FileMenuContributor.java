package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.spi.boundary.MenuContributor;
import com.github.nderwin.qd.spi.boundary.MenuItemContributor;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import module java.desktop;

@Singleton
public class FileMenuContributor implements MenuContributor {

    @Inject
    Instance<MenuItemContributor> menuItemContributors;

    @Inject
    I18n i18n;

    @Inject
    ExitAction exitAction;

    @Override
    public void contribute(final JMenuBar menuBar) {
        var fileMenu = new JMenu();
        fileMenu.setMnemonic(i18n.getMnemonic("menu.file.mnemonic"));
        fileMenu.setText(i18n.getMessage("menu.file"));

        var items = new ArrayList<MenuItemContributor>();
        menuItemContributors.forEach(items::add);
        items.sort(Comparator.comparingInt(MenuItemContributor::order));
        if (!items.isEmpty()) {
            var order = new AtomicInteger(-1);
            items.forEach((i) -> {
                if (-1 != order.get() && order.get() != i.order()) {
                    fileMenu.add(new JSeparator());
                }

                i.contribute(fileMenu);
                order.set(i.order());
            });

            fileMenu.add(new JSeparator());
        }

        var exitMenuItem = new JMenuItem();
        exitMenuItem.setAction(exitAction);
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);
    }

    @Override
    public int order() {
        return 100;
    }

}
