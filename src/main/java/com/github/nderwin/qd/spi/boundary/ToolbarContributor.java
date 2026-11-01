package com.github.nderwin.qd.spi.boundary;

import module java.desktop;

public interface ToolbarContributor extends Contributor {

    void contribute(JToolBar toolBar);

    default JButton createButton(Action action) {
        var jb = new JButton(action);
        jb.setText(null);
        jb.setFocusable(false);
        jb.putClientProperty("hideActionText", Boolean.TRUE);

        return jb;
    }

}
