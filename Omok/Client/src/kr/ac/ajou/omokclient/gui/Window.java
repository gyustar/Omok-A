package kr.ac.ajou.omokclient.gui;

import kr.ac.ajou.omokclient.communicate.ClientThread;
import processing.core.PApplet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static kr.ac.ajou.omokclient.protoocol.GameStatusData.*;

public class Window extends PApplet implements GUI {
    private Board board;
    private Button button;
    private List<PlayerInfo> players;
    private List<Stone> stones;
    private List<Box> boxes;
    private ClientThread thread;
    private int gameStatus;
    private int id;
    private boolean myTurn;
    private int dice;
    private int color;

    @Override
    public void setup() {
        connect();
    }

    @Override
    public void settings() {
        board = Board.getInstance();
        button = Button.getInstance();
        players = new CopyOnWriteArrayList<>();
        stones = new CopyOnWriteArrayList<>();
        boxes = new CopyOnWriteArrayList<>();
        myTurn = false;
        size(WINDOW_W, WINDOW_H);
    }

    @Override
    public void draw() {
        this.display(this);
        board.display(this);
        button.display(this);
        for (Box b : boxes) {
            if (boxes.size() > 1) {
                boxes.remove(b);
                continue;
            }
            b.display(this);
        }
        for (PlayerInfo p : players) p.display(this);
        for (Stone s : stones) s.display(this);
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
            thread.amReady();
        }
    }

    private boolean checkMouse() {
        int i = convertToIndex(mouseY);
        int j = convertToIndex(mouseX);

        return myTurn && checkRange() && isVacant(i, j);
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

    private boolean isVacant(int i, int j) {
        for (Stone s : stones) {
            if (s.checkStone(i, j)) return false;
        }
        return true;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
        if (gameStatus == ALL_ENTER)
            button.active();
        else if (gameStatus == RUNNING)
            boxes = boxes = new CopyOnWriteArrayList<>();
        else if (gameStatus == RESET)
            resetGame();
    }

    public void addPlayer(int id, boolean isMe) {
        players.add(new PlayerInfo(id, isMe));
        if (isMe) this.id = id;
    }

    public void readyPlayer(int id) {
        for (PlayerInfo p : players) {
            if (p.getId() == id) p.doReady();
        }
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPlayerColor(int color0, int color1) {
        for (PlayerInfo p : players) {
            if (p.hasInfo()) break;
            if (p.getId() == 0)
                p.setStoneColor(color0);
            else if (p.getId() == 1)
                p.setStoneColor(color1);
        }
    }

    public void changeTurn(int turn) {
        myTurn = (this.id == turn);
        for (PlayerInfo p : players) {
            p.changeTurn(turn);
        }
    }

    public void addStone(Stone s) {
        stones.add(s);
    }

    public void makeBox(Box b) {
        boxes.add(b);
    }

    public void activeButton() {
        button.active();
    }

    public void resetGame() {
        players = new CopyOnWriteArrayList<>();
        stones = new CopyOnWriteArrayList<>();
        boxes = new CopyOnWriteArrayList<>();
        myTurn = false;
    }

    private void connect() {
        Socket socket;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.11.27", 5000));
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
        background(WHITE_COLOR);
        mouseEvent();
    }

    public static void main(String[] args) {
        PApplet.main(Window.class);
    }
}