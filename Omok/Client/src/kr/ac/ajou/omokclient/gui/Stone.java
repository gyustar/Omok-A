package kr.ac.ajou.omokclient.gui;

import processing.core.PApplet;

public class Stone implements GUI {
    private final int i;
    private final int j;
    private final int color;

    public Stone(int i, int j, int color) {
        this.i = i;
        this.j = j;
        this.color = color;
    }

    @Override
    public void display(PApplet p) {
        if (color == BLACK) fillBlack(p);
        else if (color == WHITE) fillWhite(p);

        int x = BLOCK * (2 + j);
        int y = BLOCK * (2 + i);
        p.ellipse(x, y, DIAMETER, DIAMETER);
    }

    boolean checkStone(int i, int j) {
        return this.i == i && this.j == j;
    }
}
