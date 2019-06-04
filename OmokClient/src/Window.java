import processing.core.PApplet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Window extends PApplet implements GUI {
    private Board board;
    private Button button;
    private List<Player> players;
    private List<Stone> stones;
    private List<Box> Boxes;
    private ClientThread thread;
    private int gameState;
    private int id;
    private boolean myTurn;

    @Override
    public void setup() {
        connect();
    }

    @Override
    public void settings() {
        size(Settings.WINDOW_W, Settings.WINDOW_H);
        board = Board.getInstance();
        button = Button.getInstance();
        players = new CopyOnWriteArrayList<>();
        stones = new CopyOnWriteArrayList<>();
        Boxes = new CopyOnWriteArrayList<>();
        myTurn = false;
    }

    @Override
    public void draw() {
        this.display(this);
        board.display(this);
        button.display(this);
        for (Player p : players) p.display(this);
        for (Stone s : stones) s.display(this);
        for (Box b : Boxes) {
            if (Boxes.size() > 1) {
                Boxes.remove(b);
                continue;
            }
            b.display(this);
        }
    }

    private void mouseEvent() {
        switch (gameState) {
            case GameStateData.DEFAULT:
                cursor(ARROW);
                break;
            case GameStateData.ALL_ENTER:
                if (button.isMouseOver(this)) cursor(HAND);
                else cursor(ARROW);
                break;
            case GameStateData.RUNNING:
                if (checkMouse()) cursor(HAND);
                else cursor(ARROW);
                break;
        }
    }

    @Override
    public void mousePressed() {
        if (button.isMouseOver(this))
            button.click();
        if (gameState == GameStateData.RUNNING
                && checkMouse() && mouseButton == LEFT) {
            int i = convertToIndex(mouseY);
            int j = convertToIndex(mouseX);
            thread.putStone(i, j);
        }
    }

    @Override
    public void mouseReleased() {
        if (button.isMouseOver(this)) {
            button.release();
            button.unactive();
            thread.amReady();
        }
    }

    private boolean checkMouse() {
        int i = convertToIndex(mouseY);
        int j = convertToIndex(mouseX);

        return myTurn && checkRange() && isVacant(i, j);
    }

    private int convertToIndex(int mouse) {
        return (mouse - Settings.RANGE * 2) / Settings.BLOCK - 1;
    }

    private boolean checkRange() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (((Settings.BLOCK * 2 - Settings.RANGE + (Settings.BLOCK * i)) < mouseX) &&
                        ((Settings.BLOCK * 2 + Settings.RANGE + (Settings.BLOCK * i)) > mouseX) &&
                        ((Settings.BLOCK * 2 - Settings.RANGE + (Settings.BLOCK * j)) < mouseY) &&
                        ((Settings.BLOCK * 2 + Settings.RANGE + (Settings.BLOCK * j)) > mouseY)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isVacant(int i, int j) {
        for (Stone s : stones) {
            if (s.checkStone(i, j)) return false;
        }
        return true;
    }

    void setGameState(int gameState) {
        this.gameState = gameState;
        if (gameState == GameStateData.ALL_ENTER)
            button.active();
        else if (gameState == GameStateData.RUNNING)
            Boxes = new CopyOnWriteArrayList<>();
        else if (gameState == GameStateData.RESET)
            resetGame();
    }

    void addPlayer(int id, boolean isMe) {
        players.add(new Player(id, isMe));
        if (isMe) this.id = id;
    }

    void readyPlayer(int id) {
        for (Player p : players) {
            if (p.getId() == id) p.doReady();
        }
    }

    void setPlayerColor(int color0, int color1) {
        for (Player p : players) {
            if (p.hasInfo()) break;
            if (p.getId() == 0)
                p.setStoneColor(color0);
            else if (p.getId() == 1)
                p.setStoneColor(color1);
        }
    }

    void changeTurn(int turn) {
        myTurn = (this.id == turn);
        for (Player p : players) {
            p.changeTurn(turn);
        }
    }

    void addStone(Stone s) {
        stones.add(s);
    }

    void makeBox(Box b) {
        Boxes.add(b);
    }

    void deleteBox() {
        Boxes = new CopyOnWriteArrayList<>();
    }

    private void resetGame() {
        players = new CopyOnWriteArrayList<>();
        stones = new CopyOnWriteArrayList<>();
        Boxes = new CopyOnWriteArrayList<>();
        myTurn = false;
        button.unactive();
    }

    private void connect() {
        Socket socket;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.11.145", 5000));
            System.out.println("연결 성공\n");
            thread = new ClientThread(socket, this);
            thread.start();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display(PApplet p) {
        background(Settings.WHITE_COLOR);
        mouseEvent();
    }

    public static void main(String[] args) {
        PApplet.main(Window.class);
    }
}