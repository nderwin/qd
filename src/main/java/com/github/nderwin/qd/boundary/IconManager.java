package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;

@ApplicationScoped
public class IconManager {

    private static final String ICON_PATH = "/icons/app-icon.png";
    
    @Inject
    I18n i18n;

    private ImageIcon appIcon;

    public ImageIcon getAppIcon() {
        if (appIcon == null) {
            appIcon = loadIcon();
        }
        return appIcon;
    }

    private ImageIcon loadIcon() {
        URL imgUrl = IconManager.class.getResource(ICON_PATH);
        if (imgUrl != null) {
            return new ImageIcon(imgUrl);
        }
        
        Log.info("App icon not found at " + ICON_PATH + ". Using placeholder.");
        return createPlaceholderIcon();
    }

    private ImageIcon createPlaceholderIcon() {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Background
        g2d.setColor(new Color(0, 102, 204));
        g2d.fillRoundRect(0, 0, size, size, 15, 15);

        // Text
        var title = i18n.getMessage("app.title");
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 30));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (size - fm.stringWidth(title)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(title, x, y);

        g2d.dispose();
        return new ImageIcon(img);
    }
    
}
