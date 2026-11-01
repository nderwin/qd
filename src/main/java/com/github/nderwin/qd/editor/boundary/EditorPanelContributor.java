package com.github.nderwin.qd.editor.boundary;

import com.github.nderwin.qd.spi.boundary.ContentContributor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.awt.BorderLayout;
import javax.swing.JPanel;

@Singleton
public class EditorPanelContributor implements ContentContributor {

    @Inject
    EditorPanel panel;

    @Override
    public void contribute(final JPanel contentArea) {
        contentArea.add(panel, BorderLayout.CENTER);
    }

    @Override
    public int order() {
        return 100;
    }

}
