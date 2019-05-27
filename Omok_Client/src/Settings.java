import processing.core.PApplet;

interface Settings {

    int BLACK = 1;
    int WHITE = -1;
    int NONE = 0;
    int BLOCK = 30;
    int LINE = 15;
    int DIAMETER = BLOCK / 5 * 4;
    int GAP = BLOCK / 2;
    int BOARD = BLOCK * (LINE + 1);
    int RANGE = BLOCK / 6;
    int BUTTON_W = BOARD;
    int BUTTON_H = BLOCK * 2;
    int WINDOW_W = BOARD + BLOCK * 2;
    int WINDOW_H = BOARD + BLOCK * 3 + BUTTON_H * 3 + GAP * 2;
    int TEXT_SIZE = 20;
    int BIG_TEXT_SIZE = 30;
    int DICEBOX = 50;
    int WINBOX = 51;


    default void BlackColor(PApplet p) {
        p.fill(0);
    }

    default void WhiteColor(PApplet p) {
        p.fill(255);
    }

    default void GreyColor(PApplet p) {
        p.fill(227);
    }

    default void GreenColor(PApplet p) {
        p.fill(93, 214, 32);
    }

    default void RedColor(PApplet p) {
        p.fill(242, 65, 65);
    }

    default void BrownColor(PApplet p) {
        p.fill(203, 164, 85);
    }

    default void BoxColor(PApplet p) {
        p.fill(255, 70);
    }
}
