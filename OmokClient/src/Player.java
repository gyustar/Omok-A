import processing.core.PApplet;

public class Player implements GUI {
    private final boolean isMe;
    private final int id;
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private int stoneColor;
    private boolean ready;
    private boolean turn;

    Player(int id, boolean isMe) {
        this.id = id;
        this.isMe = isMe;

        x = Settings.BLOCK;
        y = Settings.BOARD + 2 * Settings.BLOCK + (Settings.BUTTON_H + Settings.GAP) * this.id;
        w = Settings.BUTTON_W;
        h = Settings.BUTTON_H;

        stoneColor = Settings.NONE;
        ready = false;
        turn = false;
    }

    void setStoneColor(int color) {
        if (color == Settings.BLACK || color == Settings.WHITE)
            stoneColor = color;
    }

    int getId() {
        return id;
    }

    void changeTurn(int id) {
        turn = (this.id == id);
    }

    boolean hasInfo() {
        return stoneColor != Settings.NONE;
    }

    void doReady() {
        this.ready = true;
    }

    @Override
    public void display(PApplet p) {
        drawBox(p);
        drawReady(p);
        drawName(p);

        if (turn) drawTurn(p);
        drawStoneColor(p);
    }

    private void drawBox(PApplet p) {
        fillWhite(p);
        p.rect(x, y, w, h);
    }

    private void drawReady(PApplet p) {
        if (ready) fillBlack(p);
        else fillGrey(p);

        p.textSize(Settings.TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("READY", x + w - 2 * Settings.BLOCK, y + Settings.BLOCK - 3);
    }

    private void drawName(PApplet p) {
        fillBlack(p);
        p.text("PLAYER " + id, x + Settings.BLOCK * 3, y + Settings.BLOCK - 3);

        if (isMe) {
            fillGreen(p);
            p.ellipse(x + Settings.BLOCK * 5, y + Settings.BLOCK, 5, 5);
        }
    }

    private void drawTurn(PApplet p) {
        fillRed(p);
        p.rect(x, y, Settings.BLOCK / 2, h);
    }

    private void drawStoneColor(PApplet p) {
        if (stoneColor == Settings.BLACK) {
            fillBlack(p);
            p.ellipse(x + Settings.BLOCK * 6, y + Settings.BLOCK, Settings.DIAMETER, Settings.DIAMETER);
        } else if (stoneColor == Settings.WHITE) {
            fillWhite(p);
            p.ellipse(x + Settings.BLOCK * 6, y + Settings.BLOCK, Settings.DIAMETER, Settings.DIAMETER);
        }
    }
}