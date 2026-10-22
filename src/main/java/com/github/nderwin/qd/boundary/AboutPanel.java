package com.github.nderwin.qd.boundary;

import com.formdev.flatlaf.FlatLaf;
import com.github.nderwin.qd.control.I18n;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import module java.desktop;

@Singleton
public class AboutPanel extends JPanel {

    @Inject
    I18n i18n;

    @Inject
    IconManager iconManager;

    private JButton closeButton;

    private JLabel creditsLabel;

    private JLabel iconLabel;

    private JLabel nameLabel;

    @PostConstruct
    public void init() {
        initComponents();
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        iconLabel = new JLabel();
        nameLabel = new JLabel();
        creditsLabel = new JLabel();
        closeButton = new JButton();

        setLayout(new GridBagLayout());

        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setIcon(iconManager.getAppIcon());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new Insets(20, 20, 10, 20);
        add(iconLabel, gridBagConstraints);

        nameLabel.setFont(new Font(FlatLaf.getPreferredFontFamily(), Font.BOLD, 16));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setText(i18n.getMessage("app.title"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new Insets(0, 20, 5, 20);
        add(nameLabel, gridBagConstraints);

        creditsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        creditsLabel.setText(i18n.getMessage("app.credits"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new Insets(0, 20, 20, 20);
        add(creditsLabel, gridBagConstraints);

        closeButton.setMnemonic(i18n.getMnemonic("dialog.close.mnemonic"));
        closeButton.setText(i18n.getMessage("dialog.close"));
        closeButton.addActionListener(e -> {
            var window = SwingUtilities.getWindowAncestor(this);
            if (null != window) {
                window.setVisible(false);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new Insets(0, 0, 20, 0);
        add(closeButton, gridBagConstraints);
    }

}
