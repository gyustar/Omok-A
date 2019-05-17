package com.jon.server;

import com.jon.data.*;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private static final Object MUTEX = new Object();
    private static int n = 0;
    private static Socket[] sockets = new Socket[2];
    private static OutputStream[] oss = new OutputStream[2];
    private static byte[] data = new byte[Protocol.SIZE.ordinal()];
    private int id;
    private Socket mySocket;
    private InputStream is;

    ServerThread(Socket socket) {
        this.mySocket = socket;
        synchronized (MUTEX) {
            sockets[n] = socket;
            this.id = n;

            if (this.id == 0) {
                data[Protocol.ENTER_0.ordinal()] = 1;
                data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.DEFAULT.ordinal();
            } else if (this.id == 1) {
                data[Protocol.ENTER_1.ordinal()] = 1;
                data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.ALL_ENTER.ordinal();
            }

            try {
                this.is = socket.getInputStream();
                oss[n++] = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void broadcast(byte[] data) {
        synchronized (MUTEX) {
            for (int i = 0; i < n; ++i) {
                try {
                    oss[i].write(data);
                    oss[i].flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.DEFAULT.ordinal()) {
                System.out.println("default");
                broadcast(data);
                while (true) {
                    if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_ENTER.ordinal()) {
                        break;
                    }
                }
            } else if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_ENTER.ordinal()) {
                System.out.println("entrance");
                broadcast(data);
                while (true) {
                    if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_READY.ordinal()) {
                        break;
                    }
                }
            }
        }
    }
}