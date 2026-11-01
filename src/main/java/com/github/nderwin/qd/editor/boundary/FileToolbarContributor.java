package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.spi.boundary.ToolbarContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class FileToolbarContributor implements ToolbarContributor {

    @Inject
    I18n i18n;

    @Inject
    OpenAction openAction;

    @Inject
    NewAction newAction;

    @Inject
    SaveAction saveAction;

    @Inject
    SaveAllAction saveAllAction;

    @Override
    public void contribute(final JToolBar toolBar) {
        toolBar.setName(i18n.getMessage("menu.file"));

        toolBar.add(createButton(newAction));
        toolBar.add(createButton(openAction));
        toolBar.add(createButton(saveAction));
        toolBar.add(createButton(saveAllAction));
    }

    @Override
    public int order() {
        return 100;
    }

}
