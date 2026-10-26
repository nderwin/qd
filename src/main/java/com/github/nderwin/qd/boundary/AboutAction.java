package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

import module java.desktop;

@Singleton
public class AboutAction extends AbstractAction {

    @Inject
    I18n i18n;

    @Inject
    AboutPanel aboutPanel;

    @Inject
    Instance<MainFrame> mainFrame;

    private JDialog aboutDialog;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.about"));
        putValue(MNEMONIC_KEY, i18n.getMnemonic("menuitem.about.mnemonic"));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (null == aboutDialog) {
            aboutDialog = new JDialog(
                    mainFrame.get(),
                    i18n.getMessage("dialog.about.title"),
                    true
            );
            aboutDialog.setResizable(false);
            aboutDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            aboutDialog.add(aboutPanel);
            aboutDialog.getRootPane().setDefaultButton(aboutPanel.getCloseButton());

            var escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            aboutDialog.getRootPane().registerKeyboardAction(
                    ae -> aboutPanel.getCloseButton().doClick(),
                    escape,
                    WHEN_IN_FOCUSED_WINDOW
            );

            aboutDialog.pack();
        }

        aboutDialog.setLocationRelativeTo(mainFrame.get());
        aboutDialog.setVisible(true);
    }

}
