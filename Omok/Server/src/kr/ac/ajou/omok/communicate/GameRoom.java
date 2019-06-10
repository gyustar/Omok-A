package kr.ac.ajou.omok.communicate;

import static kr.ac.ajou.omok.protocol.Protobuf.GameStatusData.Status.*;

import kr.ac.ajou.omok.util.Omok;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class GameRoom {
    private int roomNumber;
    private List<ServerThread> players;
    private int playerCount;
    private int readyCount;
    private Omok omok;

    GameRoom(int roomNumber) {
        players = new CopyOnWriteArrayList<>();
        this.roomNumber = roomNumber;
        omok = new Omok();
        playerCount = 0;
        readyCount = 0;
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
