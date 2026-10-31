package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.control.UserPreferencesStore;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

import module java.desktop;

@Singleton
public class PreferencesAction extends AbstractAction {

    @Inject
    I18n i18n;

    @Inject
    PreferencesPanel panel;

    @Inject
    Instance<MainFrame> mainFrame;

    @Inject
    UserPreferencesStore prefStore;

    private JDialog dialog;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.preferences"));
        putValue(MNEMONIC_KEY, i18n.getMnemonic("menuitem.preferences.mnemonic"));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final var current = prefStore.loadLookAndFeel();
        panel.setSelectedLookAndFeel(null == current ? LafSupport.DEFAULT_LAF : current);

        if (null == dialog) {
            dialog = new JDialog(
                    mainFrame.get(),
                    i18n.getMessage("dialog.preferences.title"),
                    true
            );
            dialog.setResizable(false);
            dialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            dialog.add(panel);

            final var okButton = panel.getOkButton();
            okButton.addActionListener(ae -> {
                final var selected = panel.getSelectedLookAndFeel();
                if (null == selected) {
                    return;
                }

                LafSupport.apply(selected);
                prefStore.saveLookAndFeel(selected);
                dialog.setVisible(false);
            });

            final var cancelButton = panel.getCancelButton();
            cancelButton.addActionListener(ae -> dialog.setVisible(false));

            dialog.getRootPane().setDefaultButton(okButton);

            final var escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            dialog.getRootPane().registerKeyboardAction(
                    ae -> cancelButton.doClick(),
                    escape,
                    WHEN_IN_FOCUSED_WINDOW
            );

            dialog.pack();
        }

        dialog.setLocationRelativeTo(mainFrame.get());
        dialog.setVisible(true);
    }

}
