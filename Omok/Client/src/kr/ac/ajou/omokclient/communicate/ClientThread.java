package kr.ac.ajou.omokclient.communicate;

import com.google.gson.Gson;
import kr.ac.ajou.omokclient.view.*;
import kr.ac.ajou.omokclient.protoocol.*;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;
    private Gson gson;
    private Window window;

    public ClientThread(Socket socket, Window window) {
        this.socket = socket;
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
                window.addQueue(protocol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}