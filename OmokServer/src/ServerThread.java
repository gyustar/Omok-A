import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


class ServerThread extends Thread {
    private static final int BLACK = 1;
    private static final int WHITE = -1;
    private static final int NONE = 0;
    private static final Object MUTEX = new Object();

    private static List<ServerThread> clients = new ArrayList<>();
    private static int n = 0;
    private static int readyCount = 0;

    private Gson gson;
    private Socket socket;
    private OutputStream os;
    private DataOutputStream dos;
    private byte[] data;
    private Omok omok;
    private int id;
    private int color;

    ServerThread(Socket socket) {
        this.socket = socket;

        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dos = new DataOutputStream(os);

        omok = new Omok();
        gson = new Gson();
        data = new byte[1024];
        color = NONE;

        synchronized (MUTEX) {
            clients.add(this);
            id = n++;
        }

        if (n == 1) sendGameStateData(GameStateData.DEFAULT);
        else if (n == 2) sendGameStateData(GameStateData.ALL_ENTER);
        sendIdData();
        sendPlayerData();
    }

    private void sendGameStateData(int gameState) {
        GameStateData gameStateData = new GameStateData(gameState);
        String json = gson.toJson(gameStateData);
        Protocol protocol = new Protocol(json, "GameStateData");
        broadcast(protocol);
    }

    private void sendIdData() {
        IdData idData = new IdData(id);
        String json = gson.toJson(idData);
        Protocol protocol = new Protocol(json, "IdData");
        sendDataToMe(protocol);
    }

    private void sendPlayerData() {
        PlayerData playerData = new PlayerData(id);
        String json = gson.toJson(playerData);
        Protocol protocol = new Protocol(json, "PlayerData");
        broadcast(protocol);
    }

    private void reset() {
        synchronized (MUTEX) {
            readyCount = 0;
            for (ServerThread t : clients) {
                t.omok = new Omok();
                t.color = NONE;
                t.sendPlayerData();
            }
        }

        GameStateData gameState = new GameStateData(GameStateData.ALL_ENTER);
        broadcast(new Protocol(gson.toJson(gameState), "GameStateData"));
    }

    private void clientExit() {
        synchronized (MUTEX) {
            n = clients.size();
            readyCount = 0;
            for (ServerThread t : clients) {
                t.omok = new Omok();
                t.id = 0;
                t.color = NONE;
                t.sendIdData();
                t.broadcast(new Protocol(gson.toJson(new GameStateData(GameStateData.RESET)),
                        "GameStateData"));
                t.broadcast(new Protocol(gson.toJson(new GameStateData(GameStateData.DEFAULT)),
                        "GameStateData"));
                t.sendPlayerData();
            }
        }
    }

    private void broadcast(Protocol protocol) {
        String json = gson.toJson(protocol);
        data = json.getBytes();
        int len = data.length;
        synchronized (MUTEX) {
            for (int i = 0; i < clients.size(); ++i) {
                ServerThread t = clients.get(i);
                try {
                    t.dos.writeInt(len);
                    t.os.write(data, 0, len);
                } catch (IOException e) {
                    clients.remove(t);
                    clientExit();
                }
            }
        }
    }

    private void sendDataToMe(Protocol protocol) {
        String str = gson.toJson(protocol);
        data = str.getBytes();
        int len = data.length;

        try {
            dos.writeInt(len);
            os.write(data, 0, len);
        } catch (IOException e) {
            synchronized (MUTEX) {
                clients.remove(this);
                clientExit();
            }
        }
    }

    private MsgData throwDice() {
        int dice0 = (int) (Math.random() * 6) + 1;
        int dice1 = (int) (Math.random() * 6) + 1;
        while (dice0 == dice1) {
            dice1 = (int) (Math.random() * 6) + 1;
        }

        if (dice0 > dice1) {
            if (id == 0) color = BLACK;
            else color = WHITE;
        } else {
            if (id == 0) color = WHITE;
            else color = BLACK;
        }

        String msg = "Player0: " + dice0 + "\n"
                + "Player1: " + dice1;
        return new MsgData(msg);
    }

    @Override
    public void run() {
        try (InputStream is = socket.getInputStream();
             DataInputStream dis = new DataInputStream(is)) {

            byte[] data = new byte[1024];

            while (true) {
                int len;
                try {
                    len = dis.readInt();
                } catch (EOFException e) {
                    synchronized (MUTEX) {
                        clients.remove(this);
                        clientExit();
                    }
                    break;
                }

                int ret = is.read(data, 0, len);
                if (ret == -1) {
                    break;
                }

                String json = new String(data, 0, len);
                Protocol protocol = gson.fromJson(json, Protocol.class);
                String type = protocol.getType();

                switch (type) {
                    case "ReadyData":
                        ReadyData readyData =
                                gson.fromJson(protocol.getData(), ReadyData.class);
                        analysisReadyData(readyData);
                        break;
                    case "StoneData":
                        StoneData stoneData =
                                gson.fromJson(protocol.getData(), StoneData.class);
                        analysisStoneData(stoneData);
                        break;
                }
            }
        } catch (IOException e) {
            synchronized (MUTEX) {
                clients.remove(this);
            }
            clientExit();
        }
    }

    private void analysisReadyData(ReadyData readyData) {
        String json = gson.toJson(readyData);
        broadcast(new Protocol(json, "ReadyData"));

        synchronized (MUTEX) {
            readyCount++;
            if (readyCount == 2) {
                long start = System.currentTimeMillis();
                long end = start;

                MsgData msgData = new MsgData("3");
                broadcast(new Protocol(gson.toJson(msgData), "MsgData"));

                while ((end - start) < 1000.0) {
                    end = System.currentTimeMillis();
                }

                start = System.currentTimeMillis();
                end = start;
                msgData = new MsgData("2");
                broadcast(new Protocol(gson.toJson(msgData), "MsgData"));
                while ((end - start) < 1000.0) {
                    end = System.currentTimeMillis();
                }

                start = System.currentTimeMillis();
                end = start;
                msgData = new MsgData("1");
                broadcast(new Protocol(gson.toJson(msgData), "MsgData"));
                while ((end - start) < 1000.0) {
                    end = System.currentTimeMillis();
                }

                start = System.currentTimeMillis();
                end = start;
                msgData = throwDice();
                broadcast(new Protocol(gson.toJson(msgData), "MsgData"));
                while ((end - start) < 2000.0) {
                    end = System.currentTimeMillis();
                }

                msgData = new MsgData("Empty");
                broadcast(new Protocol(gson.toJson(msgData), "MsgData"));

                GameStateData gameState = new GameStateData(GameStateData.RUNNING);
                broadcast(new Protocol(gson.toJson(gameState), "GameStateData"));

                ColorData colorData = null;
                TurnData turnData = null;

                if (id == 0) {
                    colorData = new ColorData(color, -color);
                    if (color == BLACK) turnData = new TurnData(0);
                    else if (color == WHITE) turnData = new TurnData(1);
                } else if (id == 1) {
                    colorData = new ColorData(-color, color);
                    if (color == BLACK) turnData = new TurnData(1);
                    else if (color == WHITE) turnData = new TurnData(0);
                }
                try {
                    broadcast(new Protocol(gson.toJson(colorData), "ColorData"));
                    broadcast(new Protocol(gson.toJson(turnData), "TurnData"));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void analysisStoneData(StoneData stoneData) {
        broadcast(new Protocol(gson.toJson(stoneData), "StoneData"));

        int i = stoneData.getI();
        int j = stoneData.getJ();
        int color = stoneData.getColor();
        omok.putStone(i, j, color);

        if (omok.winCheck(i, j)) {
            long start = System.currentTimeMillis();
            long end = start;

            String msg = "P" + id + " win!!";
            MsgData msgData = new MsgData(msg);
            broadcast(new Protocol(gson.toJson(msgData), "MsgData"));

            while ((end - start) < 3000.0) {
                end = System.currentTimeMillis();
            }

            GameStateData gameStateData = new GameStateData(GameStateData.RESET);
            broadcast(new Protocol(gson.toJson(gameStateData), "GameStateData"));

            reset();
        } else {
            TurnData turnData = new TurnData((id * -1) + 1);
            broadcast(new Protocol(gson.toJson(turnData), "TurnData"));
        }
    }
}