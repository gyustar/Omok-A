package kr.ac.ajou.omokclient.view;

import processing.core.PApplet;

import static kr.ac.ajou.omokclient.view.Constant.*;

public class RoomInfo implements GUI {
    private int x;
    private int y;
    private int w;
    private int h;

    private int roomNumber;
    private int numOfPlayer;
    private Button enterButton;

    RoomInfo(int roomNumber) {
        x = BLOCK;
        y = BLOCK * 3 + (BUTTON_H * roomNumber);
        w = BUTTON_W;
        h = BUTTON_H;

        this.roomNumber = roomNumber;
        numOfPlayer = 0;
        enterButton = new SmallButton("ENTER", x + w, y);
    }

    void setNumOfPlayer(int numOfPlayer) {
        this.numOfPlayer = numOfPlayer;
        if (this.numOfPlayer == 2) {
            enterButton.deactivate();
        } else if (this.numOfPlayer == 0 || this.numOfPlayer == 1) {
            enterButton.activate();
        }
    }

    @Override
    public void display(PApplet p) {
        fillWhite(p);
        p.rect(x, y, w, h);

        fillBlack(p);
        p.textSize(TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("ROOM #" + roomNumber, x + BLOCK * 3, y + BLOCK - 3);
        p.text(numOfPlayer + " / 2", x + w - BLOCK * 5, y + BLOCK - 3);

        enterButton.display(p);
    }

    boolean onEnterButton(PApplet p) {
        return enterButton.isMouseOver(p);
    }

    void releaseEnterButton() {
        enterButton.release();
    }

    int getRoomNumber() {
        return roomNumber;
    }
}