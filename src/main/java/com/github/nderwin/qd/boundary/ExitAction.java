package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.ApplicationExitEvent;
import com.github.nderwin.qd.control.I18n;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

@Singleton
public class ExitAction extends AbstractAction {

    @Inject
    I18n i18n;

    @Inject
    Event<ApplicationExitEvent> exitEvent;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.exit"));
        putValue(MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(i18n.getMessage("menuitem.exit.mnemonic").charAt(0)));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(i18n.getMessage("menuitem.exit.accelerator")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        exitEvent.fire(new ApplicationExitEvent());
    }

}
