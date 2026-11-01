package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.spi.boundary.ToolbarContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class UndoRedoToolbarContributor implements ToolbarContributor {

    @Inject
    I18n i18n;

    @Inject
    UndoAction undoAction;

    @Inject
    RedoAction redoAction;

    @Override
    public void contribute(final JToolBar toolBar) {
        toolBar.setName(i18n.getMessage("menu.edit"));
        toolBar.add(createButton(undoAction));
        toolBar.add(createButton(redoAction));
    }

    @Override
    public int order() {
        return 150;
    }

}
