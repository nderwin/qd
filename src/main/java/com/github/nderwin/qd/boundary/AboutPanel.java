package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@Singleton
public class AboutPanel extends JPanel {

    @Inject
    I18n i18n;

    @Inject
    IconManager iconManager;

    private JButton closeButton;

    @PostConstruct
    public void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Icon
        JLabel iconLabel = new JLabel(iconManager.getAppIcon());
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 10, 20);
        add(iconLabel, gbc);

        // App Name and Version
        JLabel nameLabel = new JLabel(i18n.getMessage("app.title"));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 16f));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 5, 20);
        add(nameLabel, gbc);

        // Credits
        JLabel creditsLabel = new JLabel(i18n.getMessage("app.credits"));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 20, 20);
        add(creditsLabel, gbc);

        // Close Button
        closeButton = new JButton(i18n.getMessage("dialog.close"));
        closeButton.setMnemonic(i18n.getMnemonic("dialog.close.mnemonic"));
        closeButton.addActionListener(e -> {
            var window = SwingUtilities.getWindowAncestor(this);
            if (null != window) {
                window.setVisible(false);
            }
        });
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(closeButton, gbc);
    }

    public JButton getCloseButton() {
        return closeButton;
    }

}
