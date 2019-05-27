import processing.core.PApplet;

class Player implements Settings {
    private final boolean mine;
    private final int id;
    private final int x;
    private final int y;
    private final int w;
    private final int h;

    private boolean ready;
    private int stoneColor;
    private boolean turn;

    Player(int id, boolean mine) {
        this.id = id;
        this.mine = mine;

        x = BLOCK;
        y = BOARD + 2 * BLOCK + (BUTTON_H + GAP) * this.id;
        w = BUTTON_W;
        h = BUTTON_H;

        ready = false;
        stoneColor = NONE;
        turn = false;
    }

    void setStoneColor(int color) {
        if (color == BLACK || color == WHITE)
            stoneColor = color;
    }

    int getId() {
        return id;
    }

    void changeTurn(int id) {
        turn = (this.id == id);
    }

    boolean hasInfo() {
        return stoneColor != NONE;
    }

    boolean isMe() {
        return mine;
    }

    void isReady() {
        ready = true;
    }

    void render(PApplet p) {
        drawBox(p);
        drawReady(p);
        drawId(p);

        if (turn) drawTurn(p);
        drawStoneColor(p);
    }

    private void drawBox(PApplet p) {
        WhiteColor(p);
        p.rect(x, y, w, h);
    }

    private void drawReady(PApplet p) {
        if (ready) BlackColor(p);
        else GreyColor(p);

        p.textSize(TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("READY", x + w - 2 * BLOCK, y + BLOCK - 3);
    }

    private void drawId(PApplet p) {
        BlackColor(p);
        p.text("PLAYER " + id, x + BLOCK * 3, y + BLOCK - 3);

        if (mine) {
            GreenColor(p);
            p.ellipse(x + BLOCK * 5, y + BLOCK, 5, 5);
        }
    }

    private void drawTurn(PApplet p) {
        RedColor(p);
        p.rect(x, y, BLOCK / 2f, h);
    }

    private void drawStoneColor(PApplet p) {
        if (stoneColor == BLACK) {
            BlackColor(p);
            p.ellipse(x + BLOCK * 6, y + BLOCK, DIAMETER, DIAMETER);
        } else if (stoneColor == WHITE) {
            WhiteColor(p);
            p.ellipse(x + BLOCK * 6, y + BLOCK, DIAMETER, DIAMETER);
        }
    }
}
