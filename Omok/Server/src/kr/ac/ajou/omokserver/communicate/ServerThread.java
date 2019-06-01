package kr.ac.ajou.omokserver.communicate;

import com.google.gson.Gson;
import kr.ac.ajou.omokserver.util.Omok;
import kr.ac.ajou.omokserver.protocol.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static kr.ac.ajou.omokserver.protocol.GameStatusData.ALL_ENTER;
import static kr.ac.ajou.omokserver.protocol.GameStatusData.DEFAULT;

class ServerThread extends Thread {
    private static final int BLACK = 1;
    private static final int WHITE = -1;
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

        synchronized (MUTEX) {
            clients.add(this);
            id = n++;
        }

        if (n == 1) sendGameStatusData(DEFAULT);
        else if (n == 2) sendGameStatusData(ALL_ENTER);
        sendIdData();
        sendPlayerData();
    }

    private void sendGameStatusData(int gameStatus) {
        GameStatusData gameStatusData = new GameStatusData(gameStatus);
        String json = gson.toJson(gameStatusData);
        Protocol protocol = new Protocol(json, "GameStatusData");
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
        omok = new Omok();
        n--;
        id = n;
        sendIdData();
        sendPlayerData();
    }

    private void broadcast(Protocol protocol) {
        String json = gson.toJson(protocol);
        data = json.getBytes();
        int len = data.length;

        synchronized (MUTEX) {
            for (ServerThread t : clients) {
                try {
                    t.dos.writeInt(len);
                    t.os.write(data, 0, len);
                } catch (IOException e) {
                    n = 0;
                    clients.remove(t);
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
                n = 0;
                clients.remove(this);
            }
        }
    }

    private MsgData throwDice() {
        int dice0 = (int) (Math.random() * 6) + 1;
        int dice1 = (int) (Math.random() * 6) + 1;
        while (dice0 == dice1) {
            dice1 = (int) (Math.random() * 6) + 1;
        }

        String msg = "P0's dice number: " + dice0 + "\n"
                + "P1's dice number: " + dice1;
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
            reset();
            synchronized (MUTEX) {
                clients.remove(this);
            }
        }
//        broadcast();
//        while (this.socket.isConnected() && !this.socket.isClosed()) {
//            try {
//                int ret = is.read(data);
//                if (ret == -1) throw new IOException();
//            } catch (IOException e) {
//                try {
//                    is.close();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//                synchronized (MUTEX) {
//                    this.reset();
//                    clients.remove(this);
//                    broadcast();
//                    break;
//                }
//            }
//            byte gameStatus = data[GAMESTATUS];
//
//            switch (gameStatus) {
//                case DEFAULT:
//                    broadcast();
//                    break;
//                case ALL_ENTER:
//                    broadcast();
//                    if (data[READY_0] == 1 && data[READY_1] == 1) {
//                        data[GAMESTATUS] = ALL_READY;
//                        throwDice();
//                        broadcast();
//                    }
//                    break;
//                case ALL_READY:
//                    if (data[READY_TO_RUN_0] == 1 && data[READY_TO_RUN_1] == 1) {
//                        data[GAMESTATUS] = RUNNING;
//                        if (data[COLOR_0] == BLACK) {
//                            data[TURN] = 0;
//                        } else if (data[COLOR_1] == BLACK) {
//                            data[TURN] = 1;
//                        }
//                        broadcast();
//                    } else broadcast();
//                    break;
//                case RUNNING:
//                    int i = data[STONE_I];
//                    int j = data[STONE_J];
//                    int color = data[STONE_C];
//                    omok.putStone(i, j, color);
//
//                    if (omok.winCheck(i, j)) {
//                        data[WINNER] = data[TURN];
//                        data[GAMESTATUS] = END;
//                    } else {
//                        data[TURN] = (byte) (data[TURN] * -1 + 1);
//                    }
//                    broadcast();
//                    break;
//                case END:
//                    data = new byte[SIZE];
//                    data[GAMESTATUS] = ALL_ENTER;
//                    omok = new Omok();
//
//                    broadcast();
//                    break;
//            }
//        }
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

                start = System.currentTimeMillis();
                end = start;
                msgData = new MsgData("Empty");
                broadcast(new Protocol(gson.toJson(msgData), "MsgData"));
            }
        }
    }

    private void analysisStoneData(StoneData stoneData) {

    }
}
