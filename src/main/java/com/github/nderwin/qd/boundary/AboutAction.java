package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

import static javax.swing.Action.MNEMONIC_KEY;

@Singleton
public class AboutAction extends AbstractAction {

    @Inject
    I18n i18n;

    @Inject
    AboutDialog aboutDialog;
    
    @Inject
    Instance<MainFrame> mainFrame;
    
    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.about"));
        putValue(MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(i18n.getMessage("menuitem.about.mnemonic").charAt(0)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        aboutDialog.showDialog(mainFrame.get());
    }

}