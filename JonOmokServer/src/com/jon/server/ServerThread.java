package com.jon.server;

import com.jon.data.*;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private static final Object MUTEX = new Object();
    private static int n = 0;
    private static byte[] data = new byte[Protocol.SIZE.ordinal()];
    private int id;
    private static Socket[] sockets = new Socket[2];
    private Socket socket;

    ServerThread(Socket socket) {
        synchronized (MUTEX) {
            this.id = n;
            sockets[n++] = socket;
            this.socket = socket;
            if (this.id == 0) {
                data[Protocol.ENTER_0.ordinal()] = 1;
                data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.DEFAULT.ordinal();
            } else if (this.id == 1) {
                data[Protocol.ENTER_1.ordinal()] = 1;
                data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.ALL_ENTER.ordinal();
            }
        }
    }

    private void broadcast() {
        synchronized (MUTEX) {
            for (int i = 0; i < n; ++i) {
                try {
                    OutputStream os = sockets[i].getOutputStream();
                    os.write(data);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("보냄");
    }

    private void inputData() {
        synchronized (MUTEX) {
            try {
                InputStream is = socket.getInputStream();
                int ret = is.read(data);
                System.out.println("받음");
                if (ret == -1) {
                    System.out.println("ret");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.DEFAULT.ordinal()) {
                System.out.println("default");
                broadcast();
                while (true) {
                    if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_ENTER.ordinal()) {
                        break;
                    }
                }
            } else if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_ENTER.ordinal()) {
                System.out.println("entrance");
                broadcast();
                inputData();
                broadcast();
                inputData();
                broadcast();
                while (true) {
                    if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_READY.ordinal()) {
                        break;
                    }
                }
            }
        }
    }
}