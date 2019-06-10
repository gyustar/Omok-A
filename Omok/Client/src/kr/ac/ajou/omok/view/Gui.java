package kr.ac.ajou.omok.view;

import processing.core.PApplet;

interface Gui {
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
