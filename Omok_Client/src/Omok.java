import processing.core.PApplet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Omok extends PApplet implements GUI, Protocol {
    private Board board;
    private Button button;
    private List<Player> players;
    private List<Stone> stones;
    private List<WinBox> boxes;
    private ClientThread thread;
    private int gameStatus;
    private int id;
    private boolean myTurn;

    @Override
    public void setup() {
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
    public void settings() {
        size(WINDOW_W, WINDOW_H);
        board = Board.getInstance();
        button = Button.getInstance();
        players = new CopyOnWriteArrayList<>();
        stones = new CopyOnWriteArrayList<>();
        boxes = new CopyOnWriteArrayList<>();
        myTurn = false;
    }

    @Override
    public void draw() {
        background(255);
        board.display(this);
        button.display(this);

        for (WinBox b : boxes) {
            b.display(this);
            if (b.isEnd()) {
                thread.canStart();
                boxes.remove(b);
            }
        }
        for (Player p : players) p.display(this);
        for (Stone s : stones) s.display(this);

        mouseEvent();
    }

    private void mouseEvent() {
        switch (gameStatus) {
            case DEFAULT:
                cursor(ARROW);
                break;
            case ALL_ENTER:
                if (button.isMouseOver(this)) cursor(HAND);
                else cursor(ARROW);
                break;
            case RUNNING:
                if (checkMouse()) cursor(HAND);
                else cursor(ARROW);
                break;
        }
    }

    @Override
    public void mousePressed() {
        if (button.isMouseOver(this))
            button.click();

        if (gameStatus == RUNNING
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
            thread.playerReady();
        }
    }

    private boolean checkMouse() {
        int i = convertToIndex(mouseY);
        int j = convertToIndex(mouseX);

        return myTurn && checkRange() && emptySpace(i, j);
    }

    private int convertToIndex(int mouse) {
        return (mouse - RANGE * 2) / BLOCK - 1;
    }

    private boolean checkRange() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (((BLOCK * 2 - RANGE + (BLOCK * i)) < mouseX) &&
                        ((BLOCK * 2 + RANGE + (BLOCK * i)) > mouseX) &&
                        ((BLOCK * 2 - RANGE + (BLOCK * j)) < mouseY) &&
                        ((BLOCK * 2 + RANGE + (BLOCK * j)) > mouseY)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean emptySpace(int i, int j) {
        for (Stone s : stones) {
            if (s.checkStone(i, j)) return false;
        }
        return true;
    }

    void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    void addPlayer(Player p, int id) {
        players.add(p);
        if (p.isMe()) this.id = id;
    }

    int howManyPlayer() {
        return players.size();
    }

    void readyPlayer(int id) {
        players.get(id).doReady();
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

    void makeBox(WinBox b) {
        boxes.add(b);
    }

    void activeButton() {
        button.active();
    }

    void resetGame() {
        players = new CopyOnWriteArrayList<>();
        stones = new CopyOnWriteArrayList<>();
    }

    @Override
    public void display(PApplet p) {

    }

    public static void main(String[] args) {
        PApplet.main(Omok.class);
    }
}