import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ServerThread extends Thread implements Protocol {

    private static List<ServerThread> players = new ArrayList<>();
    private static final Object MUTEX = new Object();
    private static final int BLACK = 1;
    private static final int WHITE = -1;
    private static int player = 0;
    private byte[] data;
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private OmokCheck omok;

    ServerThread(Socket socket) {

        this.socket = socket;

        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        omok = new OmokCheck();
        data = new byte[SIZE];

        synchronized (MUTEX) {
            players.add(this);
            int id = player++;

            for (ServerThread t : players) {
                if (id == 0) {
                    t.data[GAMESTATE] = DEFAULT;
                } else if (id == 1) {
                    t.data[GAMESTATE] = ALL_ENTER;
                }
            }
        }
    }

    private void reset() {
        omok = new OmokCheck();
        data = new byte[SIZE];
        data[GAMESTATE] = DEFAULT;
        player--;
    }

    private void broadcast() {
        synchronized (MUTEX) {
            for (ServerThread t : players) {
                try {
                    t.os.write(data);
                    t.os.flush();
                } catch (IOException e) {
                    player = 0;
                    players.remove(t);
                    try {
                        t.os.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void throwDice() {

        int dice0 = (int) (Math.random() * 6) + 1;
        int dice1 = (int) (Math.random() * 6) + 1;

        while (dice0 == dice1) {
            dice0 = (int) (Math.random() * 6) + 1;
            dice1 = (int) (Math.random() * 6) + 1;
        }
        data[DICE_0] = (byte) dice0;
        data[DICE_1] = (byte) dice1;

        if (dice0 > dice1) {
            data[COLOR_0] = BLACK;
            data[COLOR_1] = WHITE;
        } else {
            data[COLOR_0] = WHITE;
            data[COLOR_1] = BLACK;
        }
    }

    @Override
    public void run() {

        broadcast();

        while (this.socket.isConnected() && !this.socket.isClosed()) {
            try {
                int ret = is.read(data);
                if (ret == -1) throw new IOException();

            } catch (IOException e) {
                try {
                    is.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                synchronized (MUTEX) {
                    this.reset();
                    players.remove(this);
                    broadcast();
                    break;
                }
            }

            byte gameStatus = data[GAMESTATE];

            switch (gameStatus) {

                case DEFAULT:

                    broadcast();
                    break;

                case ALL_ENTER:

                    broadcast();
                    if (data[READY_0] == 1 && data[READY_1] == 1) {
                        data[GAMESTATE] = ALL_READY;
                        throwDice();
                        broadcast();
                    }
                    break;

                case ALL_READY:

                    data[GAMESTATE] = RUNNING;
                    if (data[COLOR_0] == BLACK) {
                        data[TURN] = 0;

                    } else if (data[COLOR_1] == BLACK) {
                        data[TURN] = 1;
                    }

                    try {
                        sleep(50);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    broadcast();
                    break;

                case RUNNING:

                    int i = data[STONE_I];
                    int j = data[STONE_J];
                    int color = data[STONE_C];
                    omok.putStone(i, j, color);

                    if (omok.winCheck(i, j)) {
                        data[WINNER] = data[TURN];
                        data[GAMESTATE] = END;

                    } else {
                        data[TURN] = (byte) (data[TURN] * -1 + 1);
                    }

                    broadcast();
                    break;

                case END:

                    data = new byte[SIZE];
                    data[STONE_I] = -1;
                    data[STONE_J] = -1;
                    data[TURN] = -1;
                    data[GAMESTATE] = ALL_ENTER;
                    omok = new OmokCheck();
                    broadcast();
                    break;
            }
        }
    }
}
