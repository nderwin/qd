package com.github.nderwin.qd.spi.boundary;

import module java.desktop;

public interface MenuContributor extends Contributor {

    void contribute(JMenuBar menuBar);

}
