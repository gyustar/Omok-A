package com.jon.server;

import com.jon.data.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerThread extends Thread {
    private static final int BLACK = 1;
    private static final int WHITE = -1;
    private static final Object MUTEX = new Object();
    private static List<ServerThread> clients = new ArrayList<>();
    private static int n = 0;
    private byte[] data = new byte[Protocol.SIZE.ordinal()];
    private int id;
    private Socket socket;
    private Omok omok;

    ServerThread(Socket socket) {
        this.socket = socket;
        omok = new Omok();
        synchronized (MUTEX) {
            clients.add(this);
            this.id = n++;
            for (ServerThread t : clients) {
                if (this.id == 0) {
                    t.data[Protocol.ENTER_0.ordinal()] = 1;
                    t.data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.DEFAULT.ordinal();
                } else if (this.id == 1) {
                    t.data[Protocol.ENTER_0.ordinal()] = 1;
                    t.data[Protocol.ENTER_1.ordinal()] = 1;
                    t.data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.ALL_ENTER.ordinal();
                    t.data[Protocol.STONE_I.ordinal()] = -1; // (0,0)은 가능한 인덱스이기 대문에 (-1, -1)로 초기화
                    t.data[Protocol.STONE_J.ordinal()] = -1;
                    t.data[Protocol.TURN.ordinal()] = -1;
                }
            }
        }
    }

    private void broadcast() {
        synchronized (MUTEX) {
            for (ServerThread t : clients) {
                try {
                    OutputStream os = t.socket.getOutputStream();
                    os.write(data);
                    os.flush();
                    System.out.println(id + "이" + t.id + "로 보냄");
                } catch (IOException e) {
                }
            }
        }
    }

    private void inputData() {
        try {
            InputStream is = this.socket.getInputStream();
            int ret = is.read(data);
            if (ret == -1) throw new IOException();
            System.out.println(id + "이" + id + "한테 받음");
            System.out.println(Arrays.toString(data));
        } catch (IOException e) {
        }
    }

    private void throwDice() {
        int dice0 = (int) (Math.random() * 6) + 1;
        int dice1 = (int) (Math.random() * 6) + 1;
        while (dice0 == dice1) {
            dice1 = (int) (Math.random() * 6) + 1;
        }
        data[Protocol.DICE_0.ordinal()] = (byte) dice0;
        data[Protocol.DICE_1.ordinal()] = (byte) dice1;
        if (dice0 > dice1) {
            data[Protocol.COLOR_0.ordinal()] = BLACK;
            data[Protocol.COLOR_1.ordinal()] = WHITE;
        } else {
            data[Protocol.COLOR_0.ordinal()] = WHITE;
            data[Protocol.COLOR_1.ordinal()] = BLACK;
        }
    }

    @Override
    public void run() {
        broadcast();
        while (true) {
            inputData();
            if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.DEFAULT.ordinal()) {
                broadcast();
            } else if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_ENTER.ordinal()) {
                broadcast();
                if (data[Protocol.READY_0.ordinal()] == 1 && data[Protocol.READY_1.ordinal()] == 1) {
                    data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.ALL_READY.ordinal();
                    throwDice();
                    broadcast();
                }
            } else if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_READY.ordinal()) {
                data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.RUNNING.ordinal();
                if (data[Protocol.COLOR_0.ordinal()] == BLACK)
                    data[Protocol.TURN.ordinal()] = (byte) 0;
                else data[Protocol.TURN.ordinal()] = (byte) 1;
                broadcast();
            } else if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.RUNNING.ordinal()) {
                int i = data[Protocol.STONE_I.ordinal()];
                int j = data[Protocol.STONE_J.ordinal()];
                int color = data[Protocol.STONE_C.ordinal()];
                omok.putStone(i, j, color);
                if (omok.winCheck(i, j)) {
                    data[Protocol.WINNER.ordinal()] = data[Protocol.TURN.ordinal()];
                    data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.END.ordinal();
                } else {
                    data[Protocol.TURN.ordinal()] = (byte) (data[Protocol.TURN.ordinal()] * -1 + 1);
                }
                broadcast();
            } else if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.END.ordinal()) {
                data = new byte[Protocol.SIZE.ordinal()];
                data[Protocol.ENTER_0.ordinal()] = 1;
                data[Protocol.ENTER_1.ordinal()] = 1;
                data[Protocol.STONE_I.ordinal()] = -1; // (0,0)은 가능한 인덱스이기 대문에 (-1, -1)로 초기화
                data[Protocol.STONE_J.ordinal()] = -1;
                data[Protocol.TURN.ordinal()] = -1;
                data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.ALL_ENTER.ordinal();
                omok = new Omok();
                broadcast();
            }
        }
    }
}