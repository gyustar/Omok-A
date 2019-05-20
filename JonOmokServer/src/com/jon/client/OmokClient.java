package com.jon.client;

import processing.core.PApplet;
import com.jon.data.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class OmokClient extends PApplet {
    private static final int BLACK = 1;
    private static final int WHITE = -1;
    private static final int NONE = 0;
    private static final int BLOCK = 30;
    private static final int DIAMETER = BLOCK / 5 * 4;
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
    private static int myColor = NONE;
    private static byte[][] stones = new byte[15][15];
    private static int count = 330;
    private static int countWinBox = 180;

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
        drawGameBoard();
        drawPlayerList();
        button.draw(this);

        if (data[Protocol.GAMESTATUS.ordinal()] == Protocol.DEFAULT.ordinal()) {
            cursor(ARROW);
        } else if (data[Protocol.GAMESTATUS.ordinal()] == Protocol.ALL_ENTER.ordinal()) {
            if (button.isMouseOver(this)) cursor(HAND);
            else cursor(ARROW);
        } else if (data[Protocol.GAMESTATUS.ordinal()] == Protocol.ALL_READY.ordinal()) {
            drawDice();
        } else if (data[Protocol.GAMESTATUS.ordinal()] == Protocol.RUNNING.ordinal()) {
            drawPlayerInfo();
            if (checkMouse()) cursor(HAND);
            else cursor(ARROW);
        } else if (data[Protocol.GAMESTATUS.ordinal()] == Protocol.END.ordinal()) {
            drawPlayerInfo();
            drawWinBox();
        }
    }

    private void drawWinBox() {
        if (countWinBox-- > 0) {
            fill(255, 70);
            rect(BLOCK, BLOCK * 5, BOARD, BLOCK * 8);
            fill(0);
            textSize(50);
            textAlign(CENTER, CENTER);
            String s = "Player" + (data[Protocol.WINNER.ordinal()] + 1) + " Win!!";
            text(s, BLOCK + BOARD / 2, BLOCK + BOARD / 2 - 7);
        } else if (countWinBox < 0) outputData();
    }

    private void drawGameBoard() {
        fill(203, 164, 85);
        rect(BLOCK, BLOCK, BOARD, BOARD);
        for (int i = 0; i < 15; ++i) {
            line(2 * BLOCK, (2 + i) * BLOCK, 16 * BLOCK, (2 + i) * BLOCK);
            line((2 + i) * BLOCK, 2 * BLOCK, (2 + i) * BLOCK, 16 * BLOCK);
        }
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (stones[i][j] == BLACK) {
                    fill(0);
                    ellipse(BLOCK * (2 + j), BLOCK * (2 + i), DIAMETER, DIAMETER);
                } else if (stones[i][j] == WHITE) {
                    fill(255);
                    ellipse(BLOCK * (2 + j), BLOCK * (2 + i), DIAMETER, DIAMETER);
                }
            }
        }
    }

    private void drawPlayerList() {
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
    }

    private void drawPlayerInfo() {
        if (data[Protocol.COLOR_0.ordinal()] == BLACK) {
            fill(0);
            ellipse(BLOCK * 7, 3 * BLOCK + BOARD, 20, 20);
            fill(255);
            ellipse(BLOCK * 7, 3 * BLOCK + BOARD + BUTTON_H + GAP, 20, 20);
        } else {
            fill(255);
            ellipse(BLOCK * 7, 3 * BLOCK + BOARD, 20, 20);
            fill(0);
            ellipse(BLOCK * 7, 3 * BLOCK + BOARD + BUTTON_H + GAP, 20, 20);
        }

        int k = data[Protocol.TURN.ordinal()];
        fill(242, 65, 65);
        rect(BLOCK, BOARD + 2 * BLOCK + (BUTTON_H + GAP) * k, BLOCK / 2, BUTTON_H);
    }

    private void drawDice() {
        if (count-- > 0) {
            fill(255, 70);
            rect(BLOCK, BLOCK * 5, BOARD, BLOCK * 8);
            if (count / 30 > 7) {
                fill(0);
                textSize(50);
                textAlign(CENTER, CENTER);
                text(count / 30 - 7, BLOCK + BOARD / 2, BLOCK + BOARD / 2 - 7);
            } else if (count / 30 > 2) {
                fill(255);
                rect(BLOCK * 4, BLOCK * 7, BLOCK * 4, BLOCK * 4, BLOCK / 2);
                fill(0);
                textSize(30);
                textAlign(CENTER, CENTER);
                if (id == 0)
                    text(data[Protocol.DICE_0.ordinal()], BLOCK * 6, BLOCK + BOARD / 2 - 4);
                else if (id == 1)
                    text(data[Protocol.DICE_1.ordinal()], BLOCK * 6, BLOCK + BOARD / 2 - 4);
                String s = "";
                if (myColor == BLACK) s = "BLACK!";
                else if (myColor == WHITE) s = "WHITE!";
                text(s, BLOCK * 11, BLOCK + BOARD / 2 - 4);
            } else {
                fill(0);
                textSize(30);
                textAlign(CENTER, CENTER);
                text("START!", BLOCK + BOARD / 2, BLOCK + BOARD / 2 - 4);
            }
        } else if (count <= 0) outputData();
    }

    @Override
    public void mousePressed() {
        if (button.isMouseOver(this)) button.onClick();

        if (data[Protocol.GAMESTATUS.ordinal()] == Protocol.RUNNING.ordinal() &&
                checkMouse() && mouseButton == LEFT) {
            int i = (mouseY - RANGE * 2) / BLOCK - 1;
            int j = (mouseX - RANGE * 2) / BLOCK - 1;
            data[Protocol.STONE_I.ordinal()] = (byte) i;
            data[Protocol.STONE_J.ordinal()] = (byte) j;
            data[Protocol.STONE_C.ordinal()] = (byte) myColor;
            outputData();
        }
    }

    @Override
    public void mouseReleased() {
        if (button.isMouseOver(this)) {
            button.onRelease();
            button.unactiveButton();
            if (id == 0) data[Protocol.READY_0.ordinal()] = 1;
            else if (id == 1) data[Protocol.READY_1.ordinal()] = 1;
            outputData();
        }
    }

    private boolean checkMouse() {
        boolean flag = false;

        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (((BLOCK * 2 - RANGE + (BLOCK * i)) < mouseX) &&
                        ((BLOCK * 2 + RANGE + (BLOCK * i)) > mouseX) &&
                        ((BLOCK * 2 - RANGE + (BLOCK * j)) < mouseY) &&
                        ((BLOCK * 2 + RANGE + (BLOCK * j)) > mouseY)) {
                    flag = true;
                    break;
                }
            }
        }
        int i = (mouseY - RANGE * 2) / BLOCK - 1;
        int j = (mouseX - RANGE * 2) / BLOCK - 1;

        return flag && (stones[i][j] == NONE) &&
                (data[Protocol.TURN.ordinal()] == id);
    }

    static void inputData(byte[] b) {
        data = b;
        int status = data[Protocol.GAMESTATUS.ordinal()];
        if (status == Protocol.DEFAULT.ordinal()) whenDefault();
        else if (status == Protocol.ALL_ENTER.ordinal()) whenAllEnter();
        else if (status == Protocol.ALL_READY.ordinal()) whenAllReady();
        else if (status == Protocol.RUNNING.ordinal()) whenRunning();
        else if (status == Protocol.END.ordinal()) whenEnd();
    }

    private static void whenDefault() {
        id = 0;
        players = 1;
        gameReset();
    }

    private static void whenAllEnter() {
        if (id == -1) id = 1;
        if (players != 2) players = 2;
        gameReset();
        if (data[Protocol.READY_0.ordinal()] == 1) ready[0] = true;
        if (data[Protocol.READY_1.ordinal()] == 1) ready[1] = true;
        if (!ready[id]) button.activeButton();
    }

    private static void gameReset() {
        ready = new boolean[2];
        myColor = NONE;
        count = 330;
        countWinBox = 180;
        stones = new byte[15][15];
    }

    private static void whenAllReady() {
        if (id == 0) myColor = data[Protocol.COLOR_0.ordinal()];
        else if (id == 1) myColor = data[Protocol.COLOR_1.ordinal()];
    }

    private static void whenRunning() {
        int i = data[Protocol.STONE_I.ordinal()];
        int j = data[Protocol.STONE_J.ordinal()];
        if (i != -1 && j != -1)
            stones[i][j] = data[Protocol.STONE_C.ordinal()];
    }

    private static void whenEnd() {
        int i = data[Protocol.STONE_I.ordinal()];
        int j = data[Protocol.STONE_J.ordinal()];
        stones[i][j] = data[Protocol.STONE_C.ordinal()];
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
            ClientThread thread = new ClientThread(socket);
            thread.start();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        PApplet.main("com.jon.client.OmokClient");
    }
}