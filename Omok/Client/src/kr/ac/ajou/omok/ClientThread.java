package kr.ac.ajou.omok;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class ClientThread extends Thread implements Protocol {
    private static final int NONE = 0;
    private byte[] data;
    private Socket socket;
    private Window window;
    private int id;

    ClientThread(Socket socket, Window window) {
        this.socket = socket;
        this.window = window;
        data = new byte[SIZE];
    }

    void putStone(int i, int j) {
        data[STONE_I] = (byte) i;
        data[STONE_J] = (byte) j;
        data[STONE_C] = data[COLOR_0 + data[TURN]];
        sendData();
    }

    void amReady() {
        data[READY_0 + id] = 1;
        sendData();
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStream is = socket.getInputStream();
                int ret = is.read(data);
                if (ret == -1) throw new IOException();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            int gameStatus = data[GAMESTATUS];
            window.setGameStatus(gameStatus);

            if (gameStatus == DEFAULT) whenDefault();
            else if (gameStatus == ALL_ENTER) whenAllEnter();
            else if (gameStatus == ALL_READY) whenAllReady();
            else if (gameStatus == RUNNING) whenRunning();
            else if (gameStatus == END) whenEnd();
        }
    }

    void sendData() {
        try {
            OutputStream os = socket.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("send");
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
        if (data[READY_0] == 1) window.readyPlayer(0);
        if (data[READY_1] == 1) window.readyPlayer(1);
        if (data[READY_0 + id] != 1) window.activeButton();
    }

    private void whenAllReady() {
        int dice = data[DICE_0 + id];
        int color = data[COLOR_0 + id];
        window.makeBox(new Box(dice, color));
    }

    private void whenRunning() {
        window.setPlayerColor(data[COLOR_0], data[COLOR_1]);
        System.out.println(data[TURN]);
        window.changeTurn(data[TURN]);

        int i = data[STONE_I];
        int j = data[STONE_J];
        int color = data[STONE_C];
        if (color != NONE)
            window.addStone(new Stone(i, j, color));
    }

    private void whenEnd() {
        int i = data[STONE_I];
        int j = data[STONE_J];
        int color = data[STONE_C];
        window.addStone(new Stone(i, j, color));
        window.makeBox(new Box(data[WINNER]));
    }
}
