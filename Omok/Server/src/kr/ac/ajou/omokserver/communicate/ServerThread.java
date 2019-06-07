package kr.ac.ajou.omokserver.communicate;

import com.google.gson.Gson;
import kr.ac.ajou.omokserver.protocol.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static kr.ac.ajou.omokserver.protocol.GameStatusData.*;
import static kr.ac.ajou.omokserver.protocol.LobbyData.*;

class ServerThread extends Thread {
    private static final Object MUTEX = new Object();
    private static final int BLACK = 1;
    private static final int WHITE = -1;
    private static final int NONE = 0;
    private static final int LOBBY = 1010;
    private static final int ROOM = 1011;

    private static List<ServerThread> clients = new CopyOnWriteArrayList<>();
    private static List<GameRoom> gameRooms = new CopyOnWriteArrayList<>();
    private static int n = 0;
    private static int roomCount = 0;

    private Gson gson;
    private Socket socket;
    private OutputStream os;
    private DataOutputStream dos;
    private byte[] data;
    private int id;
    private int color;
    private int position;
    private int roomNumber;
    private GameRoom gameRoom;

    ServerThread(Socket socket) {
        this.socket = socket;

        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dos = new DataOutputStream(os);

        gson = new Gson();
        data = new byte[1024];
        color = NONE;
        position = LOBBY;

        synchronized (MUTEX) {
            clients.add(this);
            n++;
            for (ServerThread t : clients) {
                if (t.getPosition() == LOBBY)
                    sendNumOfPlayer();
            }
            for (GameRoom room : gameRooms) {
                RoomInfoData roomInfoData =
                        new RoomInfoData(room.getRoomNumber(), room.getPlayerCount());
                String json = gson.toJson(roomInfoData);
                broadcastLobby(new Protocol(json, "RoomInfoData"));
            }
        }
    }

    private int getPosition() {
        return position;
    }

    void setId(int id) {
        this.id = id;
    }

    private void sendNumOfPlayer() {
        int numOfPlayer;
        synchronized (MUTEX) {
            numOfPlayer = n;
        }
        LobbyData lobbyData = new LobbyData(NUM_OF_PLAYER, numOfPlayer);
        String json = gson.toJson(lobbyData);
        broadcastLobby(new Protocol(json, "LobbyData"));
    }

    void sendGameStatusData(int gameStatus) {
        GameStatusData gameStatusData = new GameStatusData(gameStatus);
        String json = gson.toJson(gameStatusData);
        Protocol protocol = new Protocol(json, "GameStatusData");
        broadcastRoom(protocol);
    }

    void sendIdData() {
        IdData idData = new IdData(id);
        String json = gson.toJson(idData);
        Protocol protocol = new Protocol(json, "IdData");
        sendDataToMe(protocol);
    }

    void sendPlayerData() {
        PlayerData playerData = new PlayerData(id);
        String json = gson.toJson(playerData);
        Protocol protocol = new Protocol(json, "PlayerData");
        broadcastRoom(protocol);
    }

    private void reset() {
        synchronized (MUTEX) {
            for (ServerThread t : clients) {
                t.color = NONE;
                t.sendPlayerData();
            }
        }

        GameStatusData gameStatusData = new GameStatusData(ALL_ENTER);
        broadcastRoom(new Protocol(gson.toJson(gameStatusData), "GameStatusData"));
    }

    private void clientExit(ServerThread serverThread) {
        n = clients.size();
        sendNumOfPlayer();
        if (serverThread.position == ROOM) {
            synchronized (MUTEX) {
                for (ServerThread t : clients) {
                    if (t.position == ROOM && t.roomNumber == this.roomNumber) {
                        t.color = NONE;
                        t.sendDataToMe(new Protocol(gson.toJson(new GameStatusData(RESET)),
                                "GameStatusData"));
                        break;
                    }
                }
            }

            gameRoom.exitPlayer(serverThread);

            RoomInfoData roomInfoData =
                    new RoomInfoData(roomNumber, gameRoom.getPlayerCount());
            String json = gson.toJson(roomInfoData);
            broadcastLobby(new Protocol(json, "RoomInfoData"));
        }
    }

    private void broadcastRoom(Protocol protocol) {
        String json = gson.toJson(protocol);
        data = json.getBytes();
        int len = data.length;
        synchronized (MUTEX) {
            for (int i = 0; i < clients.size(); ++i) {
                ServerThread t = clients.get(i);
                if (t.position == ROOM && t.roomNumber == this.roomNumber) {
                    try {
                        t.dos.writeInt(len);
                        t.os.write(data, 0, len);
                    } catch (IOException e) {
                        clients.remove(t);
                        clientExit(t);
                    }
                }
            }
        }
    }

    private void broadcastLobby(Protocol protocol) {
        String json = gson.toJson(protocol);
        data = json.getBytes();
        int len = data.length;
        synchronized (MUTEX) {
            for (int i = 0; i < clients.size(); ++i) {
                ServerThread t = clients.get(i);
                if (t.position == LOBBY) {
                    try {
                        t.dos.writeInt(len);
                        t.os.write(data, 0, len);
                    } catch (IOException e) {
                        clients.remove(t);
                        clientExit(t);
                    }
                }
            }
        }
    }

    private void sendDataToMe(Protocol protocol) {
        String json = gson.toJson(protocol);
        data = json.getBytes();
        int len = data.length;

        try {
            dos.writeInt(len);
            os.write(data, 0, len);
        } catch (IOException e) {
            synchronized (MUTEX) {
                clients.remove(this);
                clientExit(this);
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
                    synchronized (MUTEX) {
                        clients.remove(this);
                        clientExit(this);
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
                    case "LobbyData":
                        LobbyData lobbyData =
                                gson.fromJson(protocol.getData(), LobbyData.class);
                        analysisLobbyData(lobbyData);
                        break;
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
            clientExit(this);
        }
    }

    private void analysisLobbyData(LobbyData lobbyData) {
        short header = lobbyData.getHeader();
        if (header == CREATE_ROOM) {
            int roomNumberTemp;
            synchronized (MUTEX) {
                roomNumberTemp = roomCount++;
            }
            lobbyData = new LobbyData(ENTER_ROOM, roomNumberTemp);
            String json = gson.toJson(lobbyData);
            sendDataToMe(new Protocol(json, "LobbyData"));

            position = ROOM;
            roomNumber = roomNumberTemp;
            gameRoom = new GameRoom(roomNumberTemp);
            gameRooms.add(gameRoom);
            gameRoom.enterPlayer(this);

            RoomInfoData roomInfoData = new RoomInfoData(roomNumberTemp, 1);
            json = gson.toJson(roomInfoData);
            broadcastLobby(new Protocol(json, "RoomInfoData"));
        } else if (header == ENTER_ROOM) {
            position = ROOM;
            roomNumber = lobbyData.getRoomNumber();
            gameRoom = gameRooms.get(roomNumber);
            gameRoom.enterPlayer(this);

            lobbyData = new LobbyData(ENTER_ROOM, roomNumber);
            String json = gson.toJson(lobbyData);
            sendDataToMe(new Protocol(json, "LobbyData"));

            RoomInfoData roomInfoData =
                    new RoomInfoData(roomNumber, gameRoom.getPlayerCount());
            json = gson.toJson(roomInfoData);
            broadcastLobby(new Protocol(json, "RoomInfoData"));

            PlayerData playerData = new PlayerData(this.id);
            json = gson.toJson(playerData);
            broadcastRoom(new Protocol(json, "PlayerData"));
        }
    }

    private void analysisReadyData(ReadyData readyData) {
        String json = gson.toJson(readyData);
        broadcastRoom(new Protocol(json, "ReadyData"));

        gameRoom.playerReady();
        if (gameRoom.isAllReady()) {
            long start = System.currentTimeMillis();
            long end = start;

            MsgData msgData = new MsgData("3");
            broadcastRoom(new Protocol(gson.toJson(msgData), "MsgData"));

            while ((end - start) < 1000.0) {
                end = System.currentTimeMillis();
            }

            start = System.currentTimeMillis();
            end = start;
            msgData = new MsgData("2");
            broadcastRoom(new Protocol(gson.toJson(msgData), "MsgData"));
            while ((end - start) < 1000.0) {
                end = System.currentTimeMillis();
            }

            start = System.currentTimeMillis();
            end = start;
            msgData = new MsgData("1");
            broadcastRoom(new Protocol(gson.toJson(msgData), "MsgData"));
            while ((end - start) < 1000.0) {
                end = System.currentTimeMillis();
            }

            start = System.currentTimeMillis();
            end = start;
            msgData = throwDice();
            broadcastRoom(new Protocol(gson.toJson(msgData), "MsgData"));
            while ((end - start) < 2000.0) {
                end = System.currentTimeMillis();
            }

            msgData = new MsgData("Empty");
            broadcastRoom(new Protocol(gson.toJson(msgData), "MsgData"));

            GameStatusData gameStatusData = new GameStatusData(RUNNING);
            broadcastRoom(new Protocol(gson.toJson(gameStatusData), "GameStatusData"));

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
                broadcastRoom(new Protocol(gson.toJson(colorData), "ColorData"));
                broadcastRoom(new Protocol(gson.toJson(turnData), "TurnData"));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void analysisStoneData(StoneData stoneData) {
        broadcastRoom(new Protocol(gson.toJson(stoneData), "StoneData"));

        int i = stoneData.getI();
        int j = stoneData.getJ();
        int color = stoneData.getColor();

        gameRoom.putStone(i, j, color);

        if (gameRoom.winCheck(i, j)) {
            long start = System.currentTimeMillis();
            long end = start;

            String msg = "P" + id + " win!!";
            MsgData msgData = new MsgData(msg);
            broadcastRoom(new Protocol(gson.toJson(msgData), "MsgData"));

            while ((end - start) < 3000.0) {
                end = System.currentTimeMillis();
            }

            GameStatusData gameStatusData = new GameStatusData(RESET);
            broadcastRoom(new Protocol(gson.toJson(gameStatusData), "GameStatusData"));

            gameRoom.resetGame();
            reset();
        } else {
            TurnData turnData = new TurnData((id * -1) + 1);
            broadcastRoom(new Protocol(gson.toJson(turnData), "TurnData"));
        }
    }
}