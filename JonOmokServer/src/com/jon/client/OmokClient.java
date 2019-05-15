package com.jon.client;


import processing.core.PApplet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

// (60,60) (480,60) (60,480), (480,480)

public class OmokClient extends PApplet {
    private static final int BLOCK = 30;
    private static final int GAP = BLOCK / 2;
    private static final int BOARD = BLOCK * 16;
    private static final int RANGE = BLOCK / 6;
    private static final int BUTTON_W = BOARD;
    private static final int BUTTON_H = BLOCK * 2;
    private static final int WINDOW_W = BOARD + BLOCK * 2;
    private static final int WINDOW_H = BOARD + BLOCK * 3 + BUTTON_H * 3 + GAP * 2;
    private static boolean[] readyColor = new boolean[2];

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

        for (int i = 0; i < 2; ++i) {
            fill(255);
            rect(BLOCK, BOARD + 2 * BLOCK + (BUTTON_H + GAP) * i, BUTTON_W, BUTTON_H);

            if (readyColor[i]) fill(0);
            else fill(137);

            textSize(20);
            textAlign(CENTER, CENTER);
            text("READY", BOARD - BLOCK, 3 * BLOCK + BOARD + (BUTTON_H + GAP) * i - 3);

            fill(0);
            text("PLAYER " + (i + 1), BLOCK * 4, 3 * BLOCK + BOARD + (BUTTON_H + GAP) * i - 3);
        }

        fill(203, 164, 85);
        rect(BLOCK, BLOCK, BOARD, BOARD);
        for (int i = 0; i < 15; ++i) {
            line(2 * BLOCK, (2 + i) * BLOCK, 16 * BLOCK, (2 + i) * BLOCK);
            line((2 + i) * BLOCK, 2 * BLOCK, (2 + i) * BLOCK, 16 * BLOCK);


        }
    }

    public static void main(String[] args) {
        Socket socket;
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
