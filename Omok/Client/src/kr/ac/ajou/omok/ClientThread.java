package kr.ac.ajou.omok;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

class ClientThread extends Thread implements Protocol {
    private static final Object MUTEX = new Object();
    private static final int NONE = 0;
    private byte[] data;
    private InputStream is;
    private OutputStream os;
    private Window window;
    private int id;

    ClientThread(Socket socket, Window window) {
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.window = window;
        data = new byte[SIZE];
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] bytes = new byte[SIZE];
                int ret = is.read(bytes);
                if (ret == -1) throw new IOException();
                synchronized (MUTEX) {
                    data = Arrays.copyOf(bytes, SIZE);
                }
            } catch (IOException e) {
                try {
                    is.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }
            int gameStatus;
            synchronized (MUTEX) {
                gameStatus = data[GAMESTATUS];
            }
            window.setGameStatus(gameStatus);

            if (gameStatus == DEFAULT) whenDefault();
            else if (gameStatus == ALL_ENTER) whenAllEnter();
            else if (gameStatus == ALL_READY) whenAllReady();
            else if (gameStatus == RUNNING) whenRunning();
            else if (gameStatus == END) whenEnd();
        }
    }

    private void sendData() {
        try {
            os.write(data);
            os.flush();
        } catch (IOException e) {
            try {
                is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void whenDefault() {
        if (window.howManyPlayer() != 0)
            window.resetGame();
        id = 0;
        window.addPlayer(new PlayerInfo(0, true), id);
    }

    private void whenAllEnter() {
        if (window.howManyPlayer() == 0) {
            id = 1;
            window.addPlayer(new PlayerInfo(0, false), id);
            window.addPlayer(new PlayerInfo(1, true), id);
        } else if (window.howManyPlayer() == 1) {
            window.addPlayer(new PlayerInfo(1, false), id);
        } else if (window.howManyPlayer() == 2) {
            window.resetGame();
            window.addPlayer(new PlayerInfo(0, id == 0), id);
            window.addPlayer(new PlayerInfo(1, id == 1), id);
        }
        synchronized (MUTEX) {
            if (data[READY_0] == 1) window.readyPlayer(0);
            if (data[READY_1] == 1) window.readyPlayer(1);
            if (data[READY_0 + id] != 1) window.activeButton();
        }
    }

    synchronized private void whenAllReady() {
        if (data[READY_TO_RUN_0] == 0 && data[READY_TO_RUN_1] == 0) {
            int dice = data[DICE_0 + id];
            int color = data[COLOR_0 + id];
            window.makeBox(new Box(dice, color));
        } else if (data[READY_TO_RUN_0] == 1 && data[READY_TO_RUN_1] == 1) {
            sendData();
        }
    }

    synchronized private void whenRunning() {
        window.setPlayerColor(data[COLOR_0], data[COLOR_1]);
        window.changeTurn(data[TURN]);

        int i = data[STONE_I];
        int j = data[STONE_J];
        int color = data[STONE_C];
        if (color != NONE)
            window.addStone(new Stone(i, j, color));
    }

    synchronized private void whenEnd() {
        int i = data[STONE_I];
        int j = data[STONE_J];
        int color = data[STONE_C];
        window.addStone(new Stone(i, j, color));
        window.makeBox(new Box(data[WINNER]));
    }

    synchronized void putStone(int i, int j) {
        data[STONE_I] = (byte) i;
        data[STONE_J] = (byte) j;
        data[STONE_C] = data[COLOR_0 + data[TURN]];
        sendData();
    }

    synchronized void amReady() {
        data[READY_0 + id] = 1;
        sendData();
    }

    synchronized void resetThread() {
        data = new byte[SIZE];
        data[GAMESTATUS] = END;
        sendData();
    }

    synchronized void canRun() {
        data[READY_TO_RUN_0 + id] = 1;
        sendData();
    }
}