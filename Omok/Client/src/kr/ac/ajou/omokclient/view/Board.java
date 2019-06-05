package kr.ac.ajou.omokclient.view;

import processing.core.PApplet;

import static kr.ac.ajou.omokclient.view.Constant.*;

class Board implements GUI {
    private static final Board INSTANCE = new Board();

    private final int x;
    private final int y;
    private final int w;
    private final int h;

    private Board() {
        x = BLOCK;
        y = BLOCK;
        w = BOARD;
        h = BOARD;
    }

    static Board getInstance() {
        return INSTANCE;
    }

    @Override
    public void display(PApplet p) {
        fillBrown(p);
        p.rect(x, y, w, h);

        for (int i = 0; i < POINT; ++i) {
            p.line(x + BLOCK, y + BLOCK * (i + 1), w, y + BLOCK * (i + 1));
            p.line(x + BLOCK * (i + 1), y + BLOCK, x + BLOCK * (i + 1), h);
        }
    }
}
