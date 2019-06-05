package kr.ac.ajou.omokserver.communicate;

import kr.ac.ajou.omokserver.protocol.GameStatusData;
import kr.ac.ajou.omokserver.util.Omok;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static kr.ac.ajou.omokserver.protocol.GameStatusData.ALL_ENTER;
import static kr.ac.ajou.omokserver.protocol.GameStatusData.DEFAULT;

public class GameRoom {
    private int roomNumber;
    private int playerCount;
    private Omok omok;
    private List<ServerThread> players;
    private int gameStatus;
    private int readyCount;

    GameRoom(int roomNumber) {
        players = new CopyOnWriteArrayList<>();
        this.roomNumber = roomNumber;
        omok = new Omok();
        playerCount = 0;
        readyCount = 0;
        gameStatus = DEFAULT;
    }

    void enterPlayer(ServerThread serverThread) {
        players.add(serverThread);
        serverThread.setId(playerCount++);
        if (playerCount == 1) serverThread.sendGameStatusData(DEFAULT);
        else if (playerCount == 2) serverThread.sendGameStatusData(ALL_ENTER);
        serverThread.sendIdData();
        serverThread.sendPlayerData();
    }

    void exitPlayer(ServerThread serverThread) {
        players.remove(serverThread);
        resetGame();
        playerCount--;
        if (playerCount == 1) {
            ServerThread temp = players.get(0);
            temp.setId(0);
            temp.sendGameStatusData(DEFAULT);
            temp.sendIdData();
            temp.sendPlayerData();
        }
    }

    int getPlayerCount() {
        return playerCount;
    }

    int getRoomNumber() {
        return roomNumber;
    }

    void putStone(int i, int j, int color) {
        omok.putStone(i, j, color);
    }

    boolean winCheck(int i, int j) {
        return omok.winCheck(i, j);
    }

    void playerReady() {
        readyCount++;
    }

    boolean isAllReady() {
        return readyCount == 2;
    }

    void resetGame() {
        omok = new Omok();
        readyCount = 0;
    }
}
