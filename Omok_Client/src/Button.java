import processing.core.PApplet;

class Button implements Settings {

    private final int x;
    private final int y;
    private final int w;
    private final int h;

    private final int InsideX;
    private final int InsideY;
    private final int InsideW;
    private final int InsideH;

    private boolean activation;
    private boolean onClick;

    private Button() {

        x = BLOCK;
        y = WINDOW_H - BLOCK - BUTTON_H;
        w = BUTTON_W;
        h = BUTTON_H;

        InsideX = x + 10;
        InsideY = y + 10;
        InsideW = w - 20;
        InsideH = h - 20;

        activation = false;
        onClick = false;
    }

    void render(PApplet p) {

        GreyColor(p);
        p.rect(x, y, w, h);

        if (!onClick) WhiteColor(p);
        p.rect(InsideX, InsideY, InsideW, InsideH);

        BlackColor(p);
        p.textSize(TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("READY", InsideX + InsideW / 2f, InsideY + InsideH / 2f - 3);
    }

    void click() {
        onClick = true;
    }

    void release() {
        onClick = false;
    }

    void active() {
        activation = true;
    }

    void unactive() {
        activation = false;
    }

    boolean isMouseOver(PApplet p) {
        return p.mouseX > InsideX &&
                p.mouseX < InsideX + InsideW &&
                p.mouseY > InsideY &&
                p.mouseY < InsideY + InsideH &&
                activation;
    }

    static Button getInstance() {
        return new Button();
    }
}
