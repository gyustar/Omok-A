package com.jon.client;


import com.sun.org.apache.xml.internal.serializer.OutputPropertyUtils;
import processing.core.PApplet;
import com.jon.data.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class OmokClient extends PApplet {
    private static final int BLOCK = 30;
    private static final int GAP = BLOCK / 2;
    private static final int BOARD = BLOCK * 16;
    private static final int RANGE = BLOCK / 6;
    private static final int BUTTON_W = BOARD;
    private static final int BUTTON_H = BLOCK * 2;
    private static final int WINDOW_W = BOARD + BLOCK * 2;
    private static final int WINDOW_H = BOARD + BLOCK * 3 + BUTTON_H * 3 + GAP * 2;
    private static int players = 0;
    private static int id = -1;
    private static Button button;
    private static byte[] data = new byte[Protocol.SIZE.ordinal()];
    private static boolean[] ready = new boolean[2];
    private static Socket socket;

    @Override
    public void setup() {
    }

    @Override
    public void settings() {
        size(WINDOW_W, WINDOW_H);
    }

    @Override
    public void draw() {
        background(255);

        for (int i = 0; i < players; ++i) {
            fill(255);
            rect(BLOCK, BOARD + 2 * BLOCK + (BUTTON_H + GAP) * i, BUTTON_W, BUTTON_H);

            if (ready[i]) fill(0);
            else fill(220);
            textSize(20);
            textAlign(CENTER, CENTER);
            text("READY", BOARD - BLOCK, 3 * BLOCK + BOARD + (BUTTON_H + GAP) * i - 3);

            fill(0);
            text("PLAYER " + (i + 1), BLOCK * 4, 3 * BLOCK + BOARD + (BUTTON_H + GAP) * i - 3);

            if (id == i) {
                fill(93, 214, 32);
                ellipse(BLOCK * 6, 3 * BLOCK + BOARD + (BUTTON_H + GAP) * i, 5, 5);
            }
        }

        button.draw(this);
        if (button.isMouseOver(this)) cursor(HAND);
        else cursor(ARROW);

        fill(203, 164, 85);
        rect(BLOCK, BLOCK, BOARD, BOARD);
        for (int i = 0; i < 15; ++i) {
            line(2 * BLOCK, (2 + i) * BLOCK, 16 * BLOCK, (2 + i) * BLOCK);
            line((2 + i) * BLOCK, 2 * BLOCK, (2 + i) * BLOCK, 16 * BLOCK);
        }
    }

    @Override
    public void mousePressed() {
        if (button.isMouseOver(this)) button.onClick();
    }

    @Override
    public void mouseReleased() {
        if (button.isMouseOver(this)) {
            button.onRelease();
            button.unactiveButton();
            if (id == 0) data[Protocol.READY_0.ordinal()] = 1;
            else if (id == 1) data[Protocol.READY_1.ordinal()] = 1;
            outputData();
            System.out.println("보냄");
        }
    }

    static void inputData(byte[] b) {
        data = b;
        int status = data[Protocol.GAMESTATUS.ordinal()];
        if (status == Protocol.DEFAULT.ordinal()) whenDefault();
        else if (status == Protocol.ALL_ENTER.ordinal()) {
            System.out.println("올엔터 받음");
            if (data[Protocol.READY_0.ordinal()] == 1) ready[0] = true;
            if (data[Protocol.READY_1.ordinal()] == 1) ready[1] = true;
            if (ready[0]) System.out.println("0레디");
            if (ready[1]) System.out.println("1레디");
            whenAllEnter();
        }
        else if (status == Protocol.ALL_READY.ordinal()) whenAllReady();
        else if (status == Protocol.RUNNING.ordinal()) whenRunning();
        else if (status == Protocol.END.ordinal()) whenEnd();
    }

    private static void whenDefault() {
        id = 0;
        players = 1;
    }

    private static void whenAllEnter() {
        if (id == -1) id = 1;
        if (players != 2) players = 2;
        if (!ready[id]) button.activeButton();
    }

    private static void whenAllReady() {

    }

    private static void whenRunning() {

    }

    private static void whenEnd() {

    }

    private static void outputData() {
        try {
            OutputStream os = socket.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        button = new Button.Builder("READY")
                .positionX(BLOCK)
                .positionY(WINDOW_H - BLOCK - BUTTON_H)
                .width(BUTTON_W)
                .height(BUTTON_H)
                .build();

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.11.27", 5000));
            System.out.println("연결 성공\n");

            Thread thread = new ClientThread(socket);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PApplet.main("com.jon.client.OmokClient");
    }
}