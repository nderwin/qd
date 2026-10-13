package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.ApplicationExitEvent;
import com.github.nderwin.qd.control.I18n;
import io.quarkiverse.desktop.awt.DesktopStartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

@Singleton
public class MainFrame extends JFrame {

    @Inject
    I18n i18n;
    
    @Inject
    IconManager iconManager;
    
    @Inject
    AboutAction aboutAction;
    
    @Inject
    ExitAction exitAction;

    void open(@Observes DesktopStartupEvent evt) {
        setTitle(i18n.getMessage("app.title"));
        setIconImage(iconManager.getAppIcon().getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        var bounds = getGraphicsConfiguration().getBounds();
        int minWidth = (int) Math.max(300, Math.floor(bounds.width * 0.45));
        int minHeight = (int) Math.max(200, Math.floor(bounds.height * 0.45));
        setMinimumSize(new Dimension(minWidth, minHeight));

        setJMenuBar(createMenuBar());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        var bar = new JMenuBar();

        var file = new JMenu(i18n.getMessage("menu.file"));
        file.setMnemonic(i18n.getMessage("menu.file.mnemonic").charAt(0));
        bar.add(file);

        file.add(new JMenuItem(exitAction));

        var help = new JMenu(i18n.getMessage("menu.help"));
        help.setMnemonic(i18n.getMessage("menu.help.mnemonic").charAt(0));
        bar.add(help);

        help.add(new JMenuItem(aboutAction));

        return bar;
    }

    void onExit(@Observes ApplicationExitEvent event) {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
