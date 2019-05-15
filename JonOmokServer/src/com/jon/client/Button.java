package com.jon.client;

import processing.core.PApplet;

class Button {
    private String label;
    private int color = 255;
    private boolean activation = true;
    private int x;
    private int y;
    private int w;
    private int h;

    Button(Builder builder) {
        this.label = builder.label;
        this.x = builder.x;
        this.y = builder.y;
        this.w = builder.w;
        this.h = builder.h;
    }

    static class Builder {
        private String label;
        private int x = 0;
        private int y = 0;
        private int w = 0;
        private int h = 0;

        Builder(String label) {
            this.label = label;
        }

        Builder positionX(int x) {
            this.x = x + 10;
            return this;
        }

        Builder positionY(int y) {
            this.y = y + 10;
            return this;
        }

        Builder width(int w) {
            this.w = w - 20;
            return this;
        }

        Builder height(int h) {
            this.h = h - 20;
            return this;
        }

        Button build() {
            return new Button(this);
        }
    }

    void draw(PApplet p) {
        p.fill(227);
        p.rect(x - 10, y - 10, w + 20, h + 20);
        p.fill(color);
        p.rect(x, y, w, h);
        p.fill(0);
        p.textSize(20);
        p.textAlign(p.CENTER, p.CENTER);
        p.text(label, x + w / 2, y + h / 2 - 3);
    }

    boolean isMouseOver(PApplet p) {
        return p.mouseX > x && p.mouseX < x + w &&
                p.mouseY > y && p.mouseY < y + h && this.activation;
    }

    void onClick() {
        this.color = 227;
    }

    void onRelease() {
        this.color = 255;
    }

    void changeActivation() {
        this.activation = !this.activation;
    }

    void changeLabel() {
        if (label.equals("READY")) label = "RESTART";
        else if (label.equals("RESTART")) label = "READY";
    }
}
