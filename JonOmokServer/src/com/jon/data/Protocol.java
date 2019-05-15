package com.jon.data;

import java.io.Serializable;

public class Protocol implements Serializable {
    private static final int BLACK = 1;
    private static final int WHITE = -1;
    private static final int NONE = 0;
    private GameStatus gameStatus; // DEFAULT, ALL_ENTRANCE, ALL_READY, RUNNING, END
    private ClientStatus clientStatus; // READY, RESTART, NO_READY
    private int dice;
    private boolean isBlack;
    private boolean turnBlack;
    private int[] stone;
    private int winner; // BLACK, WHITE, NONE

    public Protocol() {
        gameStatus = GameStatus.DEFAULT;
        clientStatus = ClientStatus.NO_READY;
        dice = 0;
        isBlack = false;
        turnBlack = true;
        stone = new int[2];
        winner = NONE;
    }

    public GameStatus getStatus() {
        return gameStatus;
    }

    public void allEntrance() {
        gameStatus = GameStatus.ALL_ENTRANCE;
    }
}