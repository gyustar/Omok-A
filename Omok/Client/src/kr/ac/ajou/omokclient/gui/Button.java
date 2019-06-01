package kr.ac.ajou.omokclient.gui;

import processing.core.PApplet;

import static kr.ac.ajou.omokclient.gui.Constant.*;

class Button implements GUI {
    private static final Button INSTANCE = new Button();

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

    static Button getInstance() {
        return INSTANCE;
    }

    @Override
    public void display(PApplet p) {
        fillGrey(p);
        p.rect(x, y, w, h);

        if (!onClick) fillWhite(p);
        p.rect(InnerX, InnerY, InnerW, InnerH);

        if (activation) fillBlack(p);
        else fillGrey(p);
        p.textSize(TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("READY", InnerX + InnerW / 2, InnerY + InnerH / 2 - 3);
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
}