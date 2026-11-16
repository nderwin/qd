package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.spi.boundary.UIListener;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;

import module java.desktop;

@Singleton
public class CutAction extends DefaultEditorKit.CutAction implements UIListener {

    @Inject
    I18n i18n;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.cut"));
        putValue(MNEMONIC_KEY, i18n.getMnemonic("menuitem.cut.mnemonic"));
        putValue(ACCELERATOR_KEY, i18n.getAccelerator("menuitem.cut.accelerator"));
        var icon = FontIcon.of(BootstrapIcons.SCISSORS, 16);
        putValue(SMALL_ICON, icon);
        putValue(LARGE_ICON_KEY, icon);
        putValue(SHORT_DESCRIPTION, i18n.getMessage("menuitem.cut"));
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
