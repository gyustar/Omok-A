package kr.ac.ajou.omokserver.protocol;

public class GameStatusData {
    public static final int DEFAULT = 1000;
    public static final int ALL_ENTER = 1001;
    public static final int RUNNING = 1002;
    public static final int RESET = 1003;

    private int gameStatus;

    public GameStatusData(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getGameStatus() {
        return gameStatus;
    }
}