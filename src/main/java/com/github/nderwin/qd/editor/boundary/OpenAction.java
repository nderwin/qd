package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.boundary.MainFrame;
import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.editor.control.OpenDocumentEvent;
import com.github.nderwin.qd.spi.boundary.UIListener;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;

import module java.desktop;

@Singleton
public class OpenAction extends AbstractAction implements UIListener {

    @Inject
    I18n i18n;

    @Inject
    Instance<MainFrame> mainFrame;

    @Inject
    Event<OpenDocumentEvent> openDocEvent;

    private File currentDirectory;

    @PostConstruct
    public void init() {
        putValue(NAME, i18n.getMessage("menuitem.open"));
        putValue(MNEMONIC_KEY, i18n.getMnemonic("menuitem.open.mnemonic"));
        putValue(ACCELERATOR_KEY, i18n.getAccelerator("menuitem.open.accelerator"));
        var icon = FontIcon.of(BootstrapIcons.FOLDER2_OPEN, 16);
        putValue(SMALL_ICON, icon);
        putValue(LARGE_ICON_KEY, icon);
        putValue(SHORT_DESCRIPTION, i18n.getMessage("menuitem.open"));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        var fileChooser = new JFileChooser();

        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle(i18n.getMessage("dialog.open.title"));
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                i18n.getMessage("filefilter.text"),
                "txt"
        ));
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (null != currentDirectory) {
            fileChooser.setCurrentDirectory(currentDirectory);
        }

        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(mainFrame.get())) {
            currentDirectory = fileChooser.getCurrentDirectory();

            for (var file : fileChooser.getSelectedFiles()) {
                try {
                    var text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                    openDocEvent.fire(new OpenDocumentEvent(file.toPath(), text));
                } catch (final IOException ex) {
                    Log.error("Could not open " + file, ex);
                    JOptionPane.showMessageDialog(
                            mainFrame.get(),
                            i18n.getMessage("dialog.error.open.message") + " " + file.getAbsolutePath(),
                            i18n.getMessage("dialog.error.open.title"),
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
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
