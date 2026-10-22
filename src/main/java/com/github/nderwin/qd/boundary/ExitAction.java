package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.ApplicationExitEvent;
import com.github.nderwin.qd.control.I18n;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class ExitAction extends AbstractAction {

    @Inject
    I18n i18n;

    @Inject
    Event<ApplicationExitEvent> exitEvent;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.exit"));
        putValue(MNEMONIC_KEY, i18n.getMnemonic("menuitem.exit.mnemonic"));
        putValue(ACCELERATOR_KEY, i18n.getAccelerator("menuitem.exit.accelerator"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        exitEvent.fire(new ApplicationExitEvent());
    }

}
