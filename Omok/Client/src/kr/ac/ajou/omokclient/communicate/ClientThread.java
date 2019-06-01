package kr.ac.ajou.omokclient.communicate;

import com.google.gson.Gson;
import kr.ac.ajou.omokclient.gui.*;
import kr.ac.ajou.omokclient.protoocol.*;

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

    public ClientThread(Socket socket, Window window) {
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
                    case "GameStatusData":
                        GameStatusData gameStatusData =
                                gson.fromJson(protocol.getData(), GameStatusData.class);
                        analysisGameStatusData(gameStatusData);
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

    private void analysisGameStatusData(GameStatusData gameStatusData) {
        int gameStatus = gameStatusData.getGameStatus();
        window.setGameStatus(gameStatus);
    }

    private void analysisIdData(IdData idData) {
        this.id = idData.getId();
    }

    private void analysisPlayerData(PlayerData playerData) {
        int idTemp = playerData.getId();
        if (idTemp == 0) {
            window.addPlayer(idTemp, id == idTemp);
        } else if (idTemp == 1) {
            window.addPlayer(idTemp - 1, id == idTemp - 1);
            window.addPlayer(idTemp, id == idTemp);
        }
    }

    private void analysisReadyData(ReadyData readyData) {
        window.readyPlayer(readyData.getReady());
    }

    private void analysisMsgData(MsgData msgData) {
        String msg = msgData.getMsg();

        if (msg.equals("Empty"))
            window.deleteMsgBox();
        else window.makeMsgBox(new MsgBox(msg));
    }

    private void analysisColorData(ColorData colorData) {
        int colorOfPlayer0 = colorData.getColorOfPlayer0();
        int colorOfPlayer1 = colorData.getColorOfPlayer1();
        window.setPlayerColor(colorOfPlayer0, colorOfPlayer1);

        if (this.id == 0) color = colorOfPlayer0;
        else if (this.id == 1) color = colorOfPlayer1;
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

    synchronized public void putStone(int i, int j) {
        StoneData stoneData = new StoneData(i, j, color);
        String json = gson.toJson(stoneData);
        Protocol protocol = new Protocol(json, "StoneData");
        sendData(protocol);
    }

    synchronized public void amReady() {
        ReadyData readyData = new ReadyData(id);
        String json = gson.toJson(readyData);
        Protocol protocol = new Protocol(json, "ReadyData");
        sendData(protocol);
    }
}