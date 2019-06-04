public class GameStateData {
    static final int DEFAULT = 1000;
    static final int ALL_ENTER = 1001;
    static final int RUNNING = 1002;
    static final int RESET = 1003;

    private int gameState;

    public GameStateData(int gameState) {
        this.gameState = gameState;
    }

    int getGameState() {
        return gameState;
    }
}