package com.jon.server;

public class Omok {
    private byte[][] stones;

    Omok() {
        stones = new byte[15][15];
    }

    void putStone(int i, int j, int color) {
        stones[i][j] = (byte) color;
    }

    boolean winCheck() {
        return false;
    }
}
