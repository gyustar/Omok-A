package kr.ac.ajou.omokclient.view;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import static kr.ac.ajou.omokclient.view.Constant.*;

public class Lobby implements GUI {
    private int x;
    private int y;
    private int w;
    private int h;

    private List<RoomInfo> rooms;
    private Button createRoomButton;
    private int numOfPlayer;
    private RoomInfo roomTemp;

    Lobby() {
        x = BLOCK;
        y = BLOCK * 3;
        w = BOARD;
        h = BUTTON_H * 7 + GAP * 8;

        rooms = new ArrayList<>();
        createRoomButton = new BigButton("CREATE ROOM");
        createRoomButton.activate();
    }

    void setNumOfPlayer(int numOfPlayer) {
        this.numOfPlayer = numOfPlayer;
    }

    @Override
    public void display(PApplet p) {
        fillBlack(p);
        p.textSize(TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("ROOM LIST", x + w / 2, y - BLOCK + 3);

        fillWhite(p);
        p.rect(x, y, w, h);

        for (RoomInfo roomInfo : rooms) {
            roomInfo.display(p);
        }

        fillBlack(p);
        p.textSize(TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        String text = "Players [" + numOfPlayer + "]";
        p.text(text, WINDOW_W - BLOCK * 3, WINDOW_H - BUTTON_H - BLOCK  * 3 + 5);

        createRoomButton.display(p);
    }

    boolean onCreateRoomButton(PApplet p) {
        return createRoomButton.isMouseOver(p);
    }

    boolean onEnterButton(PApplet p) {
        for (RoomInfo roomInfo : rooms) {
            if (roomInfo.onEnterButton(p)) {
                roomTemp = roomInfo;
                return true;
            }
        }
        return false;
    }

    int getRoomNumberTemp() {
        return roomTemp.getRoomNumber();
    }

    void releaseCreateRoomButton() {
        createRoomButton.release();
    }

    void releaseEnterButton() {
        roomTemp.releaseEnterButton();
    }

    void addRoom(int roomNumber, int numOfPlayer) {
        RoomInfo roomInfo = new RoomInfo(roomNumber);
        roomInfo.setNumOfPlayer(numOfPlayer);
        rooms.add(roomInfo);
    }
}
