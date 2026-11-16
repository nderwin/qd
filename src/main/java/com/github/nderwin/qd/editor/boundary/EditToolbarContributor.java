package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.spi.boundary.ToolbarContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class EditToolbarContributor implements ToolbarContributor {

    @Inject
    I18n i18n;

    @Inject
    CopyAction copyAction;

    @Inject
    CutAction cutAction;

    @Inject
    PasteAction pasteAction;

    @Override
    public void contribute(final JToolBar toolBar) {
        toolBar.setName(i18n.getMessage("menu.edit"));

        toolBar.add(createButton(cutAction));
        toolBar.add(createButton(copyAction));
        toolBar.add(createButton(pasteAction));
    }

    @Override
    public int order() {
        return 200;
    }

}
