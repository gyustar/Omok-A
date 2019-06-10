package kr.ac.ajou.omok.view;

import processing.core.PApplet;

import static kr.ac.ajou.omok.view.Constant.*;

public class SmallButton implements Button {
    private int x;
    private int y;
    private int w;
    private int h;

    private String label;
    private boolean activation;
    private boolean onClick;
    private int textSize;

    SmallButton(String label, int outlineRightX, int outlineUpY) {
        w = BLOCK * 2 + GAP;
        h = BLOCK;
        x = outlineRightX - GAP - w;
        y = outlineUpY + GAP;

        this.label = label;
        activation = false;
        onClick = false;
        textSize = TEXT_SIZE / 2;
    }

    @Override
    public void display(PApplet p) {
        if (onClick) fillGrey(p);
        else fillWhite(p);
        p.rect(x, y, w, h);

        if (activation) fillBlack(p);
        else fillGrey(p);
        p.textSize(textSize);
        p.textAlign(p.CENTER, p.CENTER);
        p.text(label, x + w / 2, y + h / 2);

//        if (this.isMouseOver(p)) {
//            p.cursor(HAND);
//            if (p.mousePressed) onClick = true;
//        } else p.cursor(ARROW);
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
