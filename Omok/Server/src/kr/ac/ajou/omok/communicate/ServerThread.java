package kr.ac.ajou.omok.communicate;

import static kr.ac.ajou.omok.protocol.Protobuf.*;
import static kr.ac.ajou.omok.protocol.Protobuf.GameStatusData.Status.*;
import static kr.ac.ajou.omok.protocol.Protobuf.LobbyData.Header.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private Socket socket;
    private OutputStream os;
    private DataOutputStream dos;
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
                RoomInfoData roomInfoData = RoomInfoData
                        .newBuilder()
                        .setRoomNumber(room.getRoomNumber())
                        .setNumOfPlayer(room.getPlayerCount())
                        .build();
                Protocol protocol = Protocol
                        .newBuilder()
                        .setType("RoomInfoData")
                        .setData(roomInfoData.toByteString())
                        .build();
                broadcastLobby(protocol);
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
        LobbyData lobbyData = LobbyData
                .newBuilder()
                .setHeader(NUM_OF_PLAYER)
                .setNumOfPlayer(numOfPlayer)
                .build();
        Protocol protocol = Protocol
                .newBuilder()
                .setType("LobbyData")
                .setData(lobbyData.toByteString())
                .build();
        broadcastLobby(protocol);
    }

    void sendGameStatusData(GameStatusData.Status gameStatus) {
        GameStatusData gameStatusData = GameStatusData
                .newBuilder()
                .setGameStatus(gameStatus)
                .build();
        Protocol protocol = Protocol
                .newBuilder()
                .setType("GameStatusData")
                .setData(gameStatusData.toByteString())
                .build();
        broadcastRoom(protocol);
    }

    void sendIdData() {
        IdData idData = IdData
                .newBuilder()
                .setId(id)
                .build();
        Protocol protocol = Protocol
                .newBuilder()
                .setType("IdData")
                .setData(idData.toByteString())
                .build();
        sendDataToMe(protocol);
    }

    void sendPlayerData() {
        PlayerData playerData = PlayerData
                .newBuilder()
                .setId(id)
                .build();
        Protocol protocol = Protocol
                .newBuilder()
                .setType("PlayerData")
                .setData(playerData.toByteString())
                .build();
        broadcastRoom(protocol);
    }

    private void reset() {
        synchronized (MUTEX) {
            for (ServerThread t : clients) {
                t.color = NONE;
                t.sendPlayerData();
            }
        }

        GameStatusData gameStatusData = GameStatusData
                .newBuilder()
                .setGameStatus(ALL_ENTER)
                .build();
        Protocol protocol = Protocol
                .newBuilder()
                .setType("GameStatusData")
                .setData(gameStatusData.toByteString())
                .build();
        broadcastRoom(protocol);
    }

    private void clientExit(ServerThread serverThread) {
        n = clients.size();
        sendNumOfPlayer();
        if (serverThread.position == ROOM) {
            synchronized (MUTEX) {
                for (ServerThread t : clients) {
                    if (t.position == ROOM && t.roomNumber == this.roomNumber) {
                        t.color = NONE;
                        GameStatusData gameStatusData = GameStatusData
                                .newBuilder()
                                .setGameStatus(RESET)
                                .build();
                        Protocol protocol = Protocol
                                .newBuilder()
                                .setType("GameStatusData")
                                .setData(gameStatusData.toByteString())
                                .build();
                        t.sendDataToMe(protocol);
                        break;
                    }
                }
            }

            gameRoom.exitPlayer(serverThread);

            RoomInfoData roomInfoData = RoomInfoData
                    .newBuilder()
                    .setRoomNumber(roomNumber)
                    .setNumOfPlayer(gameRoom.getPlayerCount())
                    .build();
            Protocol protocol = Protocol
                    .newBuilder()
                    .setType("RoomInfoData")
                    .setData(roomInfoData.toByteString())
                    .build();
            broadcastLobby(protocol);
        }
    }

    private void broadcastRoom(Protocol protocol) {
        byte[] data = protocol.toByteArray();
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
        byte[] data = protocol.toByteArray();
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
        byte[] data = protocol.toByteArray();
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
        return MsgData
                .newBuilder()
                .setMsg(msg)
                .build();
    }

    @Override
    public void run() {
        try (InputStream is = socket.getInputStream();
             DataInputStream dis = new DataInputStream(is)) {

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

                byte[] data = new byte[len];

                int ret = is.read(data, 0, len);
                if (ret == -1) {
                    break;
                }

                Protocol protocol = Protocol.parseFrom(data);
                String type = protocol.getType();

                switch (type) {
                    case "LobbyData":
                        LobbyData lobbyData =
                                LobbyData.parseFrom(protocol.getData());
                        analysisLobbyData(lobbyData);
                        break;
                    case "ReadyData":
                        ReadyData readyData =
                                ReadyData.parseFrom(protocol.getData());
                        analysisReadyData(readyData);
                        break;
                    case "StoneData":
                        StoneData stoneData =
                                StoneData.parseFrom(protocol.getData());
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
        LobbyData.Header header = lobbyData.getHeader();
        if (header == CREATE_ROOM) {
            int roomNumberTemp;
            synchronized (MUTEX) {
                roomNumberTemp = roomCount++;
            }
            lobbyData = LobbyData
                    .newBuilder()
                    .setHeader(ENTER_ROOM)
                    .setRoomNumber(roomNumberTemp)
                    .build();
            Protocol protocol = Protocol
                    .newBuilder()
                    .setType("LobbyData")
                    .setData(lobbyData.toByteString())
                    .build();
            sendDataToMe(protocol);

            position = ROOM;
            roomNumber = roomNumberTemp;
            gameRoom = new GameRoom(roomNumberTemp);
            gameRooms.add(gameRoom);
            gameRoom.enterPlayer(this);

            RoomInfoData roomInfoData = RoomInfoData
                    .newBuilder()
                    .setRoomNumber(roomNumberTemp)
                    .setNumOfPlayer(1)
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("RoomInfoData")
                    .setData(roomInfoData.toByteString())
                    .build();
            broadcastLobby(protocol);
        } else if (header == ENTER_ROOM) {
            position = ROOM;
            roomNumber = lobbyData.getRoomNumber();
            gameRoom = gameRooms.get(roomNumber);
            gameRoom.enterPlayer(this);

            lobbyData = LobbyData
                    .newBuilder()
                    .setHeader(ENTER_ROOM)
                    .setRoomNumber(roomNumber)
                    .build();
            Protocol protocol = Protocol
                    .newBuilder()
                    .setType("LobbyData")
                    .setData(lobbyData.toByteString())
                    .build();
            sendDataToMe(protocol);

            RoomInfoData roomInfoData = RoomInfoData
                    .newBuilder()
                    .setRoomNumber(roomNumber)
                    .setNumOfPlayer(gameRoom.getPlayerCount())
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("RoomInfoData")
                    .setData(roomInfoData.toByteString())
                    .build();
            broadcastLobby(protocol);

            PlayerData playerData = PlayerData
                    .newBuilder()
                    .setId(this.id)
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("PlayerData")
                    .setData(playerData.toByteString())
                    .build();
            broadcastRoom(protocol);
        }
    }

    private void analysisReadyData(ReadyData readyData) {
        Protocol protocol = Protocol
                .newBuilder()
                .setType("ReadyData")
                .setData(readyData.toByteString())
                .build();
        broadcastRoom(protocol);

        gameRoom.playerReady();
        if (gameRoom.isAllReady()) {
            long start = System.currentTimeMillis();
            long end = start;

            MsgData msgData = MsgData
                    .newBuilder()
                    .setMsg("3")
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("MsgData")
                    .setData(msgData.toByteString())
                    .build();
            broadcastRoom(protocol);

            while ((end - start) < 1000.0) {
                end = System.currentTimeMillis();
            }

            start = System.currentTimeMillis();
            end = start;
            msgData = MsgData
                    .newBuilder()
                    .setMsg("2")
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("MsgData")
                    .setData(msgData.toByteString())
                    .build();
            broadcastRoom(protocol);

            while ((end - start) < 1000.0) {
                end = System.currentTimeMillis();
            }

            start = System.currentTimeMillis();
            end = start;
            msgData = MsgData
                    .newBuilder()
                    .setMsg("1")
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("MsgData")
                    .setData(msgData.toByteString())
                    .build();
            broadcastRoom(protocol);

            while ((end - start) < 1000.0) {
                end = System.currentTimeMillis();
            }

            start = System.currentTimeMillis();
            end = start;
            msgData = throwDice();
            protocol = Protocol
                    .newBuilder()
                    .setType("MsgData")
                    .setData(msgData.toByteString())
                    .build();
            broadcastRoom(protocol);

            while ((end - start) < 2000.0) {
                end = System.currentTimeMillis();
            }

            msgData = MsgData
                    .newBuilder()
                    .setMsg("Empty")
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("MsgData")
                    .setData(msgData.toByteString())
                    .build();
            broadcastRoom(protocol);

            GameStatusData gameStatusData = GameStatusData
                    .newBuilder()
                    .setGameStatus(RUNNING)
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("GameStatusData")
                    .setData(gameStatusData.toByteString())
                    .build();
            broadcastRoom(protocol);

            ColorData colorData;
            TurnData turnData;

            if (id == 0) {
                colorData = ColorData
                        .newBuilder()
                        .setColorOfPlayer0(color)
                        .setColorOfPlayer1(-color)
                        .build();
                if (color == BLACK) {
                    turnData = TurnData
                            .newBuilder()
                            .setTurn(0)
                            .build();
                } else {
                    turnData = TurnData
                            .newBuilder()
                            .setTurn(1)
                            .build();
                }
            } else {
                colorData = ColorData
                        .newBuilder()
                        .setColorOfPlayer0(-color)
                        .setColorOfPlayer1(color)
                        .build();
                if (color == BLACK) {
                    turnData = TurnData
                            .newBuilder()
                            .setTurn(1)
                            .build();
                } else {
                    turnData = TurnData
                            .newBuilder()
                            .setTurn(0)
                            .build();
                }
            }
            Protocol colorProtocol = Protocol
                    .newBuilder()
                    .setType("ColorData")
                    .setData(colorData.toByteString())
                    .build();
            Protocol turnProtocol = Protocol
                    .newBuilder()
                    .setType("TurnData")
                    .setData(turnData.toByteString())
                    .build();
            broadcastRoom(colorProtocol);
            broadcastRoom(turnProtocol);

        }
    }

    private void analysisStoneData(StoneData stoneData) {
        Protocol protocol = Protocol
                .newBuilder()
                .setType("StoneData")
                .setData(stoneData.toByteString())
                .build();
        broadcastRoom(protocol);

        int i = stoneData.getI();
        int j = stoneData.getJ();
        int color = stoneData.getColor();

        gameRoom.putStone(i, j, color);

        if (gameRoom.winCheck(i, j)) {
            long start = System.currentTimeMillis();
            long end = start;

            String msg = "P" + id + " win!!";
            MsgData msgData = MsgData
                    .newBuilder()
                    .setMsg(msg)
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("MsgData")
                    .setData(msgData.toByteString())
                    .build();
            broadcastRoom(protocol);

            while ((end - start) < 3000.0) {
                end = System.currentTimeMillis();
            }

            GameStatusData gameStatusData = GameStatusData
                    .newBuilder()
                    .setGameStatus(RESET)
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("GameStatusData")
                    .setData(gameStatusData.toByteString())
                    .build();
            broadcastRoom(protocol);

            gameRoom.resetGame();
            reset();
        } else {
            TurnData turnData = TurnData
                    .newBuilder()
                    .setTurn((id * -1) + 1)
                    .build();
            protocol = Protocol
                    .newBuilder()
                    .setType("TurnData")
                    .setData(turnData.toByteString())
                    .build();
            broadcastRoom(protocol);
        }
    }
}