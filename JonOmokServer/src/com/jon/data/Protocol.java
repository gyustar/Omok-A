package com.jon.data;

import java.io.Serializable;

public class Protocol implements Serializable {
    private static final int BLACK = 1;
    private static final int WHITE = -1;
    private static final int NONE = 0;
    private GameStatus gameStatus; // DEFAULT, ALL_ENTRANCE, ALL_READY, RUNNING, END
    private boolean entrance[];
    private boolean ready[];
    private boolean restart[];
    private int dice[];
    private int color[];
    private int stoneIndex[];
//    private boolean isBlack;
//    private int turn;
    private int winner;

    public Protocol() {
        gameStatus = GameStatus.DEFAULT;
        entrance = new boolean[2];
        ready = new boolean[2];
        restart = new boolean[2];
        dice = new int[2];
        color = new int[2];
        stoneIndex = new int[2];
//        isBlack = false;
//        turn = 0;
        winner = NONE;
    }

    public void entrancePlayer(int id) {
        entrance[id] = true;
        if (entrance[0] && entrance[1])
            gameStatus = GameStatus.ALL_ENTRANCE;
    }

    public GameStatus getStatus() {
        return gameStatus;
    }
}