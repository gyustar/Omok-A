import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class ClientThread extends Thread implements Protocol {
    private static final int NONE = 0;
    private byte[] data;
    private InputStream is;
    private OutputStream os;
    private OmokGame omokgame;
    private int id;

    ClientThread(Socket socket, OmokGame omokgame) {
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.omokgame = omokgame;
        data = new byte[SIZE];
    }

    void putStone(int i, int j) {
        data[STONE_I] = (byte) i;
        data[STONE_J] = (byte) j;
        data[STONE_C] = data[COLOR_0 + data[TURN]];
        sendData();
    }

    void playerReady() {
        data[READY_0 + id] = 1;
        sendData();
    }

    void canStart() {
        sendData();
    }

    @Override
    public void run() {
        while (true) {
            try {
                int ret = is.read(data);
                if (ret == -1) throw new IOException();
            } catch (IOException e) {
                try {
                    is.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }

            int gameStatus = data[GAMESTATE];
            omokgame.setGameState(gameStatus);

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
        if (omokgame.allPlayer() != 0)
            omokgame.resetGame();
        id = 0;
        omokgame.addPlayer(new Player(0, true), id);
    }

    private void whenAllEnter() {
        if (omokgame.allPlayer() == 0) {
            id = 1;
            omokgame.addPlayer(new Player(0, false), id);
            omokgame.addPlayer(new Player(1, true), id);

        } else if (omokgame.allPlayer() == 1) {
            omokgame.addPlayer(new Player(1, false), id);

        } else if (omokgame.allPlayer() == 2) {
            omokgame.resetGame();
            omokgame.addPlayer(new Player(0, id == 0), id);
            omokgame.addPlayer(new Player(1, id == 1), id);
        }
        if (data[READY_0] == 1) omokgame.readyPlayer(0);
        if (data[READY_1] == 1) omokgame.readyPlayer(1);
        if (data[READY_0 + id] != 1) omokgame.activeButton();
    }

    private void whenAllReady() {
        int dice = data[DICE_0 + id];
        int color = data[COLOR_0 + id];
        omokgame.drawBox(new Box(dice, color));
    }

    private void whenRunning() {
        omokgame.setPlayerColor(data[COLOR_0], data[COLOR_1]);
        System.out.println(data[TURN]);
        omokgame.changeTurn(data[TURN]);

        int i = data[STONE_I];
        int j = data[STONE_J];
        int color = data[STONE_C];

        if (color != NONE)
            omokgame.addStone(new Stone(i, j, color));
    }

    private void whenEnd() {
        int i = data[STONE_I];
        int j = data[STONE_J];
        int color = data[STONE_C];
        omokgame.addStone(new Stone(i, j, color));
        omokgame.drawBox(new Box(data[WINNER]));
    }
}
