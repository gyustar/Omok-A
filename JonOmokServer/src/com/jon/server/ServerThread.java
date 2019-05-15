package com.jon.server;

import com.jon.data.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
    private static int n = 0;
    private static Socket[] sockets = new Socket[2];
    private static ObjectOutputStream[] ooss = new ObjectOutputStream[2];
    private Socket mySocket;
    private Protocol data;
    private ObjectInputStream ois;

    ServerThread(Socket socket) {
        this.mySocket = socket;
        sockets[n] = socket;
        this.data = new Protocol();

        try {
            this.ois = new ObjectInputStream(socket.getInputStream());
            ooss[n++] = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void broadcast(Protocol data) {
        for (ObjectOutputStream os : ooss) {
            try {
                os.writeObject(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (n == 2) {
            data.allEntrance();
            broadcast(data);
        }
        while (true) {
            int a = 0;
            a++;
            if (a == 500) break;
        }
    }
}
