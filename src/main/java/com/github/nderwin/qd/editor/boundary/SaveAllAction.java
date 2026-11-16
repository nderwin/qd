package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.editor.control.SaveAllDocumentEvent;
import com.github.nderwin.qd.spi.boundary.UIListener;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;

import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;

import module java.desktop;

@Singleton
public class SaveAllAction extends AbstractAction implements UIListener {

    @Inject
    I18n i18n;

    @Inject
    Event<SaveAllDocumentEvent> saveAllDocEvent;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.saveall"));
        putValue(MNEMONIC_KEY, i18n.getMnemonic("menuitem.saveall.mnemonic"));
        putValue(ACCELERATOR_KEY, i18n.getAccelerator("menuitem.saveall.accelerator"));
        var icon = FontIcon.of(BootstrapIcons.FILES_ALT, 16);
        putValue(SMALL_ICON, icon);
        putValue(LARGE_ICON_KEY, icon);
        putValue(SHORT_DESCRIPTION, i18n.getMessage("menuitem.saveall"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        saveAllDocEvent.fire(new SaveAllDocumentEvent());
    }

    @Override
    public Optional<FontIcon> getSmallIcon() {
        return Optional.of((FontIcon) getValue(SMALL_ICON));
    }

    @Override
    public Optional<FontIcon> getLargeIcon() {
        return Optional.of((FontIcon) getValue(LARGE_ICON_KEY));
    }

}
