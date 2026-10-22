package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.I18n;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.net.URL;

import module java.desktop;

@ApplicationScoped
public class IconManager {

    private static final String ICON_PATH = "/icons/app-icon.png";

    @Inject
    I18n i18n;

    private ImageIcon appIcon;

    public synchronized ImageIcon getAppIcon() {
        if (null == appIcon) {
            appIcon = loadIcon();
        }
        return appIcon;
    }

    private ImageIcon loadIcon() {
        URL imgUrl = IconManager.class.getResource(ICON_PATH);

        if (null == imgUrl) {
            Log.debug("App icon not found at " + ICON_PATH + ". Using placeholder.");
            return createPlaceholderIcon();
        }

        try {
            var image = ImageIO.read(imgUrl);
            if (null != image) {
                return new ImageIcon(image);
            }

            Log.debug("App icon at " + ICON_PATH + " could not be decoded. Using placeholder.");
        } catch (final IOException ex) {
            Log.warn("Failed to read app icon from " + ICON_PATH, ex);
        }

        return createPlaceholderIcon();
    }

    private ImageIcon createPlaceholderIcon() {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        try {
            // Background
            g2d.setColor(new Color(204, 102, 0));
            g2d.fillRoundRect(0, 0, size, size, 15, 15);

            // Text
            var title = i18n.getMessage("app.title");
            if (2 < title.length()) {
                title = title.substring(0, 2);
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 30));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (size - fm.stringWidth(title)) / 2;
            int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(title, x, y);

            return new ImageIcon(img);
        } finally {
            g2d.dispose();
        }

    }

}
