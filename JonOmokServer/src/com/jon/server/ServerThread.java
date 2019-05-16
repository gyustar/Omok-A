package com.jon.server;

import com.jon.data.GameStatus;
import com.jon.data.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
    private static final Object MUTEX = new Object();
    private static int n = 0;
    private static Socket[] sockets = new Socket[2];
    private static ObjectOutputStream[] ooss = new ObjectOutputStream[2];
    private static Protocol data = new Protocol();;
    private int id;
    private Socket mySocket;
    private ObjectInputStream ois;

    ServerThread(Socket socket) {
        this.mySocket = socket;
        synchronized (MUTEX) {
            sockets[n] = socket;
            this.id = n;
            data.entrancePlayer(this.id);
            try {
                this.ois = new ObjectInputStream(socket.getInputStream());
                ooss[n++] = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void broadcast(Protocol data) {
        synchronized (MUTEX) {
            for (int i = 0; i < n; ++i) {
                try {
                    ooss[i].writeObject(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
//            synchronized (MUTEX) {
                if (data.getStatus() == GameStatus.DEFAULT) {
                    broadcast(data);
                } else if (data.getStatus() == GameStatus.ALL_ENTRANCE) {
                    broadcast(data);
                }
//            }
        }
    }
}
