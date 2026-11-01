package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.editor.control.NewDocumentEvent;
import com.github.nderwin.qd.control.I18n;
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
public class NewAction extends AbstractAction implements UIListener {

    @Inject
    I18n i18n;

    @Inject
    Event<NewDocumentEvent> newDocEvent;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.new"));
        putValue(MNEMONIC_KEY, i18n.getMnemonic("menuitem.new.mnemonic"));
        putValue(ACCELERATOR_KEY, i18n.getAccelerator("menuitem.new.accelerator"));
        var icon = FontIcon.of(BootstrapIcons.FILE_EARMARK_PLUS, 16);
        putValue(SMALL_ICON, icon);
        putValue(LARGE_ICON_KEY, icon);
        putValue(SHORT_DESCRIPTION, i18n.getMessage("menuitem.new"));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        newDocEvent.fire(new NewDocumentEvent());
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
