package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.editor.control.UndoDocumentEvent;
import com.github.nderwin.qd.spi.boundary.UIListener;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;

import module java.desktop;

@Singleton
public class UndoAction extends AbstractAction implements UIListener {

    @Inject
    I18n i18n;

    @Inject
    Event<UndoDocumentEvent> undoEvent;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.undo"));
        putValue(MNEMONIC_KEY, i18n.getMnemonic("menuitem.undo.mnemonic"));
        putValue(ACCELERATOR_KEY, i18n.getAccelerator("menuitem.undo.accelerator"));
        var icon = FontIcon.of(BootstrapIcons.ARROW_COUNTERCLOCKWISE, 16);
        putValue(SMALL_ICON, icon);
        putValue(LARGE_ICON_KEY, icon);
        putValue(SHORT_DESCRIPTION, i18n.getMessage("menuitem.undo"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        undoEvent.fire(new UndoDocumentEvent());
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
