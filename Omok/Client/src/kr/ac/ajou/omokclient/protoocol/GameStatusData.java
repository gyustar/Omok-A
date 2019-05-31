package kr.ac.ajou.omokclient.protoocol;

public class GameStatusData {
    public static final int DEFAULT = 1000;
    public static final int RUNNING = 1001;
    public static final int END = 10002;

    private int gameStatus;

    public GameStatusData(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getGameStatus() {
        return gameStatus;
    }
}
