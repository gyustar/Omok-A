import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;
    private OutputStream os;
    private DataOutputStream dos;
    private Gson gson;
    private Window window;
    private int id;
    private int color;

    ClientThread(Socket socket, Window window) {
        this.socket = socket;

        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dos = new DataOutputStream(os);

        this.window = window;
        gson = new Gson();
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
                    case "GameStateData":
                        GameStateData gameStateData =
                                gson.fromJson(protocol.getData(), GameStateData.class);
                        analysisGameStateData(gameStateData);
                        break;
                    case "IdData":
                        IdData idData =
                                gson.fromJson(protocol.getData(), IdData.class);
                        analysisIdData(idData);
                        break;
                    case "PlayerData":
                        PlayerData playerData =
                                gson.fromJson(protocol.getData(), PlayerData.class);
                        analysisPlayerData(playerData);
                        break;
                    case "ReadyData":
                        ReadyData readyData =
                                gson.fromJson(protocol.getData(), ReadyData.class);
                        analysisReadyData(readyData);
                        break;
                    case "MsgData":
                        MsgData msgData =
                                gson.fromJson(protocol.getData(), MsgData.class);
                        analysisMsgData(msgData);
                    case "ColorData":
                        ColorData colorData =
                                gson.fromJson(protocol.getData(), ColorData.class);
                        analysisColorData(colorData);
                        break;
                    case "TurnData":
                        TurnData turnData =
                                gson.fromJson(protocol.getData(), TurnData.class);
                        analysisTurnData(turnData);
                        break;
                    case "StoneData":
                        StoneData stoneData =
                                gson.fromJson(protocol.getData(), StoneData.class);
                        analysisStoneData(stoneData);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendData(Protocol protocol) {
        try {
            byte[] data;
            String json = gson.toJson(protocol);
            data = json.getBytes();
            int len = data.length;
            dos.writeInt(len);
            os.write(data, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analysisGameStateData(GameStateData gameStateData) {
        int gameState = gameStateData.getGameState();
        window.setGameState(gameState);
    }

    private void analysisIdData(IdData idData) {
        this.id = idData.getId();
    }

    private void analysisPlayerData(PlayerData playerData) {
        int tempId = playerData.getId();
        if (tempId == 0) {
            window.addPlayer(tempId, id == tempId);
        } else if (tempId == 1) {
            window.addPlayer(tempId - 1, id == tempId - 1);
            window.addPlayer(tempId, id == tempId);
        }
    }

    private void analysisReadyData(ReadyData readyData) {
        window.readyPlayer(readyData.getReady());
    }

    private void analysisMsgData(MsgData msgData) {
        String msg = msgData.getMsg();
        if (msg.equals("Empty"))
            window.deleteMsgBox();
        else window.makeMsgBox(new Box(msg));
    }

    private void analysisColorData(ColorData colorData) {
        int player0Color = colorData.getPlayer0Color();
        int player1Color = colorData.getPlayer1Color();
        window.setPlayerColor(player0Color, player1Color);
        if (this.id == 0) color = player0Color;
        else if (this.id == 1) color = player1Color;
    }

    private void analysisTurnData(TurnData turnData) {
        int turn = turnData.getTurn();
        window.changeTurn(turn);
    }

    private void analysisStoneData(StoneData stoneData) {
        int i = stoneData.getI();
        int j = stoneData.getJ();
        int color = stoneData.getColor();
        window.addStone(new Stone(i, j, color));
    }

    synchronized void putStone(int i, int j) {
        StoneData stoneData = new StoneData(i, j, color);
        String json = gson.toJson(stoneData);
        Protocol protocol = new Protocol(json, "StoneData");
        sendData(protocol);
    }

    synchronized void amReady() {
        ReadyData readyData = new ReadyData(id);
        String json = gson.toJson(readyData);
        Protocol protocol = new Protocol(json, "ReadyData");
        sendData(protocol);
    }
}