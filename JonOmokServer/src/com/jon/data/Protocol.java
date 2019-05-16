package com.jon.data;

import java.io.Serializable;

public class Protocol implements Serializable {
    private static final int BLACK = 1;
    private static final int WHITE = -1;
    private static final int NONE = 0;
    private GameStatus gameStatus; // DEFAULT, ALL_ENTRANCE, ALL_READY, RUNNING, END
    private ClientStatus clientStatus; // READY, RESTART, NO_READY
    private int players;
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
        players = 0;
    }

    public void setPlayers(int players) {
        if(players == 1) this.players = players;
        else if (players == 2) this.players = players;
        else this.players = 0;
    }

    public int getPlayers() {
        return players;
    }

    public GameStatus getStatus() {
        return gameStatus;
    }

    public void allEntrance() {
        gameStatus = GameStatus.ALL_ENTRANCE;
    }
}