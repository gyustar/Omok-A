import processing.core.PApplet;

class Board implements GUI {
    private static final Board INSTANCE = new Board();

    private final int x;
    private final int y;
    private final int w;
    private final int h;

    private Board() {
        x = Settings.BLOCK;
        y = Settings.BLOCK;
        w = Settings.BOARD;
        h = Settings.BOARD;
    }

    static Board getInstance() {
        return INSTANCE;
    }

    @Override
    public void display(PApplet p) {
        fillBrown(p);
        p.rect(x, y, w, h);

        for (int i = 0; i < Settings.POINT; ++i) {
            p.line(x + Settings.BLOCK, y + Settings.BLOCK * (i + 1), w, y + Settings.BLOCK * (i + 1));
            p.line(x + Settings.BLOCK * (i + 1), y + Settings.BLOCK, x + Settings.BLOCK * (i + 1), h);
        }
    }
}
