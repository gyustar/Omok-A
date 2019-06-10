package kr.ac.ajou.omok.communicate;

import static kr.ac.ajou.omok.protocol.Protobuf.*;

import kr.ac.ajou.omok.view.*;

import java.io.*;
import java.net.Socket;

public class ReceiveThread extends Thread {
    private Socket socket;
    private Window window;

    public ReceiveThread(Socket socket, Window window) {
        this.socket = socket;
        this.window = window;
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
                    break;
                }
                byte[] data = new byte[len];

                int ret = is.read(data, 0, len);
                if (ret == -1) {
                    break;
                }

                Protocol protocol = Protocol.parseFrom(data);

                window.addQueue(protocol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}