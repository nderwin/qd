package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.boundary.MainFrame;
import com.github.nderwin.qd.control.I18n;
import com.github.nderwin.qd.editor.control.NewDocumentEvent;
import com.github.nderwin.qd.editor.control.OpenDocumentEvent;
import com.github.nderwin.qd.editor.control.RedoDocumentEvent;
import com.github.nderwin.qd.editor.control.SaveAllDocumentEvent;
import com.github.nderwin.qd.editor.control.SaveDocumentEvent;
import com.github.nderwin.qd.editor.control.UndoDocumentEvent;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.awt.event.MouseEvent;
import javax.swing.event.DocumentEvent;

import module java.base;
import module java.desktop;

@Singleton
public class EditorPanel extends JTabbedPane {

    private static final String DOCUMENT_PATH = "document.path";
    private static final String DOCUMENT_SAVED_TEXT = "document.savedText";
    private static final String DOCUMENT_UNDO_MANAGER = "document.undoManager";
    private static final String TAB_TITLE_BASE = "tab.title.base";
    private static final String TAB_TOOLTIP = "tab.tooltip";
    private static final String TAB_LABEL = "tab.label";

    @Inject
    I18n i18n;

    @Inject
    Instance<MainFrame> mainFrame;

    @Inject
    Instance<SaveAction> saveAction;

    @Inject
    Instance<SaveAllAction> saveAllAction;

    @Inject
    Instance<UndoAction> undoAction;

    @Inject
    Instance<RedoAction> redoAction;

    private File currentDirectory;

    private int untitledCounter = 1;

    private final CloseKeyAdapter closeKeyAdapter = new CloseKeyAdapter();

    @PostConstruct
    public void init() {
        setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
        addChangeListener(_ -> refreshActions());
        addKeyListener(closeKeyAdapter);

        refreshActions();
    }

    public void onNewDocument(@Observes final NewDocumentEvent evt) {
        var textArea = createTextArea("");
        textArea.putClientProperty(TAB_TITLE_BASE, null);
        var base = i18n.getMessage("tab.untitled");
        var title = (1 == untitledCounter) ? base : base + " " + untitledCounter;
        untitledCounter++;
        textArea.putClientProperty(TAB_TITLE_BASE, title);
        textArea.putClientProperty(TAB_TOOLTIP, null);
        textArea.putClientProperty(DOCUMENT_SAVED_TEXT, "");

        var scrollPane = new JScrollPane(textArea);

        addTab(title, scrollPane);
        var index = indexOfComponent(scrollPane);
        setTabComponentAt(index, createClosableTab(title, scrollPane, ""));
        setSelectedComponent(scrollPane);
        syncTab(textArea);
        refreshActions();
    }

    public void onOpenDocument(@Observes final OpenDocumentEvent evt) {
        /* TODO
            - size limit for opening the file?
            - options for line wrap
            - open file read only? (textArea.setEditable())
            - Popup menu on tab
                - Close All action Ctrl+Shift+W
                - Close Other action
         */
        var path = evt.path().toAbsolutePath().normalize();

        int existing = indexOfOpenFile(path);
        if (0 <= existing) {
            setSelectedIndex(existing);
            return;
        }

        var textArea = createTextArea(evt.content());
        textArea.putClientProperty(DOCUMENT_PATH, path);
        textArea.putClientProperty(DOCUMENT_SAVED_TEXT, evt.content());

        var scrollPane = new JScrollPane(textArea);
        var title = evt.path().getFileName().toString();
        var tooltip = evt.path().toAbsolutePath().toString();

        textArea.putClientProperty(TAB_TITLE_BASE, title);
        textArea.putClientProperty(TAB_TOOLTIP, tooltip);

        addTab(title, scrollPane);
        var index = indexOfComponent(scrollPane);
        setTabComponentAt(index, createClosableTab(title, scrollPane, tooltip));
        setToolTipTextAt(index, tooltip);
        setSelectedComponent(scrollPane);
        syncTab(textArea);
        refreshActions();
    }

    public void onSaveDocument(@Observes final SaveDocumentEvent evt) {
        saveDocument(getActiveTextArea());
    }

    public void onSaveAllDocument(@Observes final SaveAllDocumentEvent evt) {
        for (int t = 0; t < getTabCount(); t++) {
            var component = getComponentAt(t);
            if (component instanceof final JScrollPane scrollPane
                    && scrollPane.getViewport().getView() instanceof final JTextArea textArea
                    && isDirty(textArea)) {
                // TODO - if not previously saved, prompt user for new file location and name
                saveDocument(textArea);
            }
        }
    }

    public void onUndoDocument(@Observes final UndoDocumentEvent evt) {
        var textArea = getActiveTextArea();
        if (null == textArea) {
            return;
        }

        var undoManager = (UndoManager) textArea.getClientProperty(DOCUMENT_UNDO_MANAGER);
        if (null != undoManager && undoManager.canUndo()) {
            try {
                undoManager.undo();
            } catch (final CannotUndoException ex) {
                Log.warn("Could not undo", ex);
            }
        }
    }

    public void onRedoDocument(@Observes final RedoDocumentEvent evt) {
        var textArea = getActiveTextArea();
        if (null == textArea) {
            return;
        }

        var undoManager = (UndoManager) textArea.getClientProperty(DOCUMENT_UNDO_MANAGER);
        if (null != undoManager && undoManager.canRedo()) {
            try {
                undoManager.redo();
            } catch (final CannotRedoException ex) {
                Log.warn("Could not redo", ex);
            }
        }
    }

    private JTextArea createTextArea(final String text) {
        var textArea = new JTextArea(text);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        var undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        textArea.putClientProperty(DOCUMENT_UNDO_MANAGER, undoManager);
        textArea.putClientProperty(DOCUMENT_SAVED_TEXT, text);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                onDocumentChanged(textArea);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                onDocumentChanged(textArea);
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                onDocumentChanged(textArea);
            }
        });
        textArea.addKeyListener(closeKeyAdapter);

        return textArea;
    }

    private Component createClosableTab(final String title, final Component content, final String tooltip) {
        var tab = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tab.setOpaque(false);

        var label = new JLabel(title);
        label.setOpaque(false);
        label.setToolTipText(tooltip);

        if (content instanceof final JScrollPane scrollPane
                && scrollPane.getViewport().getView() instanceof final JTextArea textArea) {
            textArea.putClientProperty(TAB_LABEL, label);
        }

        var selectTab = new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                var index = indexOfTabComponent(tab);
                if (0 <= index && index != getSelectedIndex()) {
                    setSelectedIndex(index);
                }
            }

        };
        tab.addMouseListener(selectTab);
        label.addMouseListener(selectTab);

        var close = new JButton("×");
        close.setFocusable(false);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.setMargin(new Insets(0, 0, 0, 0));
        close.getAccessibleContext().setAccessibleName(i18n.getMessage("tab.close.accessible.name"));
        close.addActionListener(e -> {
            var idx = indexOfComponent(content);
            if (0 <= idx) {
                saveAndClose(idx);
            }
        });

        tab.add(label);
        tab.add(close);

        return tab;
    }

    private void saveAndClose(final int selectedIndex) {
        var component = getComponentAt(selectedIndex);

        if (component instanceof final JScrollPane scrollPane
                && scrollPane.getViewport().getView() instanceof final JTextArea textArea) {
            if (isDirty(textArea)) {
                var frame = (null == mainFrame.get()) ? null : mainFrame.get();
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        i18n.getMessage("dialog.save.unsaved.prompt"),
                        i18n.getMessage("dialog.save.title"),
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    saveDocument(textArea);
                } else if (choice == JOptionPane.NO_OPTION) {
                    removeTabAt(selectedIndex);
                    refreshActions();
                }
                // Else: Cancel (do nothing)
            } else {
                // Not dirty, safe to remove
                removeTabAt(selectedIndex);
                refreshActions();
            }
        }
    }

    private int indexOfOpenFile(final Path path) {
        for (int i = 0; i < getTabCount(); i++) {
            var component = getComponentAt(i);
            if (component instanceof final JScrollPane scrollPane
                    && scrollPane.getViewport().getView() instanceof final JTextArea textArea) {
                var existing = textArea.getClientProperty(DOCUMENT_PATH);
                if (existing instanceof final Path p
                        && p.equals(path)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void onDocumentChanged(final JTextArea textArea) {
        syncTab(textArea);
        refreshActions();
    }

    private JTextArea getActiveTextArea() {
        final int idx = getSelectedIndex();
        if (0 > idx) {
            return null;
        }

        final var component = getComponentAt(idx);
        if (component instanceof final JScrollPane scrollPane
                && scrollPane.getViewport().getView() instanceof final JTextArea textArea) {
            return textArea;
        }

        return null;
    }

    private boolean isDirty(final JTextArea textArea) {
        if (null == textArea) {
            return false;
        }

        final var saved = textArea.getClientProperty(DOCUMENT_SAVED_TEXT);
        final var savedText = (saved instanceof final String s) ? s : "";
        return !Objects.equals(textArea.getText(), savedText);
    }

    private void syncTab(final JTextArea textArea) {
        final int idx = indexOfComponent(getScrollPane(textArea));
        if (0 > idx) {
            return;
        }

        var base = textArea.getClientProperty(TAB_TITLE_BASE);
        final var baseTitle = (base instanceof final String s && !s.isBlank()) ? s : "Untitled";
        final var title = isDirty(textArea) ? baseTitle + " *" : baseTitle;

        setTitleAt(idx, title);

        final var tooltipObj = textArea.getClientProperty(TAB_TOOLTIP);
        if (tooltipObj instanceof final String tooltip && !tooltip.isBlank()) {
            setToolTipTextAt(idx, tooltip);
        }

        final var labelObj = textArea.getClientProperty(TAB_LABEL);
        if (labelObj instanceof final JLabel label) {
            label.setText(title);
        }
    }

    private JScrollPane getScrollPane(final JTextArea textArea) {
        if (null == textArea) {
            return null;
        }

        for (int i = 0; i < getTabCount(); i++) {
            final var component = getComponentAt(i);
            if (component instanceof final JScrollPane scrollPane
                    && scrollPane.getViewport().getView() == textArea) {
                return scrollPane;
            }
        }

        return null;
    }

    private void refreshActions() {
        final var textArea = getActiveTextArea();
        final boolean hasDoc = null != textArea;

        if (!hasDoc) {
            saveAction.get().setEnabled(false);
            saveAllAction.get().setEnabled(false);
            undoAction.get().setEnabled(false);
            redoAction.get().setEnabled(false);
            return;
        }

        boolean dirty = false;
        UndoManager undoManager = null;
        if (hasDoc) {
            dirty = isDirty(textArea);
            final var umObj = textArea.getClientProperty(DOCUMENT_UNDO_MANAGER);
            if (umObj instanceof final UndoManager um) {
                undoManager = um;
            }
        }

        try {
            saveAction.get().setEnabled(dirty);
            saveAllAction.get().setEnabled(dirty);
        } catch (final Exception _) {
        }

        boolean canUndo = null != undoManager && undoManager.canUndo();
        boolean canRedo = null != undoManager && undoManager.canRedo();

        try {
            undoAction.get().setEnabled(canUndo);
        } catch (final Exception _) {
        }

        try {
            redoAction.get().setEnabled(canRedo);
        } catch (final Exception _) {
        }
    }

    private void saveDocument(final JTextArea textArea) {
        if (null == textArea) {
            return;
        }

        var pathObj = textArea.getClientProperty(DOCUMENT_PATH);
        Path path = (pathObj instanceof final Path p) ? p : null;

        if (null == path) {
            final var frame = mainFrame.get();
            final var fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(i18n.getMessage("dialog.save.title"));
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                    i18n.getMessage("filefilter.text"),
                    "txt"
            ));
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (null != currentDirectory) {
                fileChooser.setCurrentDirectory(currentDirectory);
            }

            if (JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(frame)) {
                return;
            }

            currentDirectory = fileChooser.getCurrentDirectory();
            path = fileChooser.getSelectedFile().toPath();
            var ext = ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
            if (!path.endsWith("." + ext)) {
                path = path.resolve(path.toString() + "." + ext);
            }
            textArea.putClientProperty(DOCUMENT_PATH, path);
        }

        try {
            Files.writeString(path, textArea.getText(), StandardCharsets.UTF_8);
            textArea.putClientProperty(DOCUMENT_SAVED_TEXT, textArea.getText());

            final var name = path.getFileName().toString();
            textArea.putClientProperty(TAB_TITLE_BASE, name);
            textArea.putClientProperty(TAB_TOOLTIP, path.toAbsolutePath().toString());

            final var idx = indexOfComponent(getScrollPane(textArea));
            if (0 <= idx) {
                setToolTipTextAt(idx, path.toAbsolutePath().toString());
            }

            syncTab(textArea);
            refreshActions();
        } catch (final IOException ex) {
            Log.error("Could not save " + path, ex);
            final var frame = (null == mainFrame) ? null : mainFrame.get();
            JOptionPane.showMessageDialog(
                    frame,
                    i18n.getMessage("dialog.error.save.message") + " " + path,
                    i18n.getMessage("dialog.error.save.title"),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private class CloseKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(final KeyEvent e) {
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W) {
                int selectedIndex = getSelectedIndex();
                if (0 <= selectedIndex) {
                    saveAndClose(selectedIndex);
                }
            }
        }

    }
}
