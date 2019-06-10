package kr.ac.ajou.omok.view;

import processing.core.PApplet;

public interface Button extends Gui {
    void click();
    void release();
    void activate();
    void deactivate();
    boolean isMouseOver(PApplet p);
}
