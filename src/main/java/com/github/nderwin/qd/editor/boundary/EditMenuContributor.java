package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.spi.boundary.MenuContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class EditMenuContributor implements MenuContributor {

    @Inject
    I18n i18n;

    @Inject
    CopyAction copyAction;

    @Inject
    CutAction cutAction;

    @Inject
    PasteAction pasteAction;

    @Inject
    RedoAction redoAction;

    @Inject
    UndoAction undoAction;

    @Override
    public void contribute(final JMenuBar menuBar) {
        var editMenu = new JMenu();
        editMenu.setMnemonic(i18n.getMnemonic("menu.edit.mnemonic"));
        editMenu.setText(i18n.getMessage("menu.edit"));

        editMenu.add(new JMenuItem(undoAction));
        editMenu.add(new JMenuItem(redoAction));
        editMenu.add(new JSeparator());

        editMenu.add(new JMenuItem(cutAction));
        editMenu.add(new JMenuItem(copyAction));
        editMenu.add(new JMenuItem(pasteAction));

        menuBar.add(editMenu);
    }

    @Override
    public int order() {
        return 200;
    }

}
