import processing.core.PApplet;

class Button implements GUI {
    private final int x;
    private final int y;
    private final int w;
    private final int h;

    private final int InnerX;
    private final int InnerY;
    private final int InnerW;
    private final int InnerH;

    private boolean activation;
    private boolean onClick;

    private Button() {
        x = BLOCK;
        y = WINDOW_H - BLOCK - BUTTON_H;
        w = BUTTON_W;
        h = BUTTON_H;

        InnerX = x + 10;
        InnerY = y + 10;
        InnerW = w - 20;
        InnerH = h - 20;

        activation = false;
        onClick = false;
    }

    @Override
    public void display(PApplet p) {

        GreyColor(p);
        p.rect(x, y, w, h);

        if (!onClick) WhiteColor(p);
        p.rect(InnerX, InnerY, InnerW, InnerH);

        BlackColor(p);
        p.textSize(TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("READY", InnerX + InnerW / 2f, InnerY + InnerH / 2f - 3);
    }

    void click() {
        onClick = true;
    }

    void release() {
        onClick = false;
    }

    void active() {
        this.activation = true;
    }

    void unactive() {
        this.activation = false;
    }

    boolean isMouseOver(PApplet p) {
        return p.mouseX > InnerX && p.mouseX < InnerX + InnerW &&
                p.mouseY > InnerY && p.mouseY < InnerY + InnerH && this.activation;
    }

    static Button getInstance() {
        return new Button();
    }
}
