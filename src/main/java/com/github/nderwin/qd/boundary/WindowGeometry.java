package com.github.nderwin.qd.boundary;

public record WindowGeometry(
        int x,
        int y,
        int w,
        int h
        ) {

    public static final WindowGeometry DEFAULT = new WindowGeometry(100, 100, 1000, 700);

}
