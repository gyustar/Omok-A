package kr.ac.ajou.omok;

import processing.core.PApplet;

interface GUI {
    int BLACK = 1;
    int WHITE = -1;
    int NONE = 0;
    int BLOCK = 30;
    int POINT = 15;
    int DIAMETER = BLOCK / 5 * 4;
    int GAP = BLOCK / 2;
    int BOARD = BLOCK * (POINT + 1);
    int RANGE = BLOCK / 6;
    int BUTTON_W = BOARD;
    int BUTTON_H = BLOCK * 2;
    int WINDOW_W = BOARD + BLOCK * 2;
    int WINDOW_H = BOARD + BLOCK * 3 + BUTTON_H * 3 + GAP * 2;
    int TEXT_SIZE = 20;
    int TEXT_SIZE_BIG = 30;
    int DICEBOX = 50;
    int WINBOX = 51;
    int WHITE_COLOR = 255;

    default void fillBlack(PApplet p) {
        p.fill(0);
    }

    default void fillWhite(PApplet p) {
        p.fill(255);
    }

    default void fillGrey(PApplet p) {
        p.fill(227);
    }

    default void fillGreen(PApplet p) {
        p.fill(93, 214, 32);
    }

    default void fillRed(PApplet p) {
        p.fill(242, 65, 65);
    }

    default void fillBrown(PApplet p) {
        p.fill(203, 164, 85);
    }

    default void fillTransparent(PApplet p) {
        p.fill(255, 70);
    }

    void display(PApplet p);
}
