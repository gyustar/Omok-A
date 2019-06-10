package kr.ac.ajou.omok.view;

import processing.core.PApplet;

import static kr.ac.ajou.omok.view.Constant.*;

public class BigButton implements Button {
    private int x;
    private int y;
    private int w;
    private int h;

    private int outlineX;
    private int outlineY;
    private int outlineW;
    private int outlineH;

    private String label;
    private boolean activation;
    private boolean onClick;
    private int textSize;

    BigButton(String label) {
        outlineX = BLOCK;
        outlineY = WINDOW_H - BLOCK - BUTTON_H;
        outlineW = BUTTON_W;
        outlineH = BUTTON_H;

        x = outlineX + 10;
        y = outlineY + 10;
        w = outlineW - 20;
        h = outlineH - 20;

        this.label = label;
        activation = false;
        onClick = false;
        textSize = TEXT_SIZE;
    }

    @Override
    public void display(PApplet p) {
        fillGrey(p);
        p.rect(outlineX, outlineY, outlineW, outlineH);

        if (onClick) fillGrey(p);
        else fillWhite(p);
        p.rect(x, y, w, h);

        if (activation) fillBlack(p);
        else fillGrey(p);
        p.textSize(textSize);
        p.textAlign(p.CENTER, p.CENTER);
        p.text(label, x + w / 2, y + h / 2 - 3);
    }

    @Override
    public void click() {
        onClick = true;
    }

    @Override
    public void release() {
        onClick = false;
    }

    @Override
    public void activate() {
        activation = true;
    }

    @Override
    public void deactivate() {
        activation = false;
    }

    @Override
    public boolean isMouseOver(PApplet p) {
        return p.mouseX > x && p.mouseX < x + w &&
                p.mouseY > y && p.mouseY < y + h && activation;
    }
}