package com.github.nderwin.qd.spi.boundary;

import module java.desktop;

public interface MenuItemContributor extends Contributor {

    void contribute(JMenu menu);

}
