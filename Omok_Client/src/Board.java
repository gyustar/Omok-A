import processing.core.PApplet;

class Board implements Settings {
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

    void render(PApplet p) {

        BrownColor(p);
        p.rect(x, y, w, h);

        for (int i = 0; i < LINE; ++i) {
            p.line(x + BLOCK, y + BLOCK * (i + 1), w, y + BLOCK * (i + 1));
            p.line(x + BLOCK * (i + 1), y + BLOCK, x + BLOCK * (i + 1), h);
        }
    }

    static Board getInstance() {
        return new Board();
    }
}