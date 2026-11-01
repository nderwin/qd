package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.control.UserPreferencesStore;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.stream.IntStream;

import module java.desktop;

@Singleton
public class PreferencesPanel extends JPanel {

    @Inject
    I18n i18n;

    @Inject
    UserPreferencesStore prefStore;

    private JComboBox<UIManager.LookAndFeelInfo> lookAndFeelComboBox;

    private JLabel lookAndFeelLabel;

    private JButton okButton;

    private JButton cancelButton;

    @PostConstruct
    public void init() {
        setLayout(new GridBagLayout());

        lookAndFeelLabel = new JLabel();
        lookAndFeelLabel.setText(i18n.getMessage("label.lookandfeel") + ":");
        lookAndFeelLabel.setDisplayedMnemonic(i18n.getMnemonic("label.lookandfeel.mnemonic"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(12, 12, 6, 8);
        add(lookAndFeelLabel, gbc);

        lookAndFeelComboBox = new JComboBox<>(LafSupport.installed());
        lookAndFeelComboBox.setPrototypeDisplayValue(
                new UIManager.LookAndFeelInfo("Prototype Look and Feel", "")
        );
        lookAndFeelComboBox.setEditable(false);
        lookAndFeelComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    final JList<?> list,
                    final Object value,
                    final int index,
                    final boolean isSelected,
                    final boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof final UIManager.LookAndFeelInfo info) {
                    setText(info.getName());
                }

                return this;
            }

        });
        lookAndFeelLabel.setLabelFor(lookAndFeelComboBox);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(12, 0, 6, 12);
        add(lookAndFeelComboBox, gbc);

        okButton = new JButton(i18n.getMessage("dialog.ok"));
        okButton.setMnemonic(i18n.getMnemonic("dialog.ok.mnemonic"));

        cancelButton = new JButton(i18n.getMessage("dialog.cancel"));
        cancelButton.setMnemonic(i18n.getMnemonic("dialog.cancel.mnemonic"));

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(8, 12, 12, 12);
        add(buttonPanel, gbc);

        setSelectedLookAndFeel(prefStore.loadLookAndFeel());
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public String getSelectedLookAndFeel() {
        final var selected = (UIManager.LookAndFeelInfo) lookAndFeelComboBox.getSelectedItem();
        return null == selected ? null : selected.getClassName();
    }

    public void setSelectedLookAndFeel(final String lookAndFeelClassName) {
        if (null == lookAndFeelClassName) {
            lookAndFeelComboBox.setSelectedIndex(0);
            return;
        }

        int index = IntStream.range(0, lookAndFeelComboBox.getItemCount())
                .filter((i) -> lookAndFeelClassName.equals(lookAndFeelComboBox.getItemAt(i).getClassName()))
                .findFirst()
                .orElse(0);

        lookAndFeelComboBox.setSelectedIndex(index);
    }

}
