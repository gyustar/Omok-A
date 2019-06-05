package kr.ac.ajou.omokclient.view;

import processing.core.PApplet;

public interface Button extends GUI {
    void click();
    void release();
    void activate();
    void deactivate();
    boolean isMouseOver(PApplet p);
}
