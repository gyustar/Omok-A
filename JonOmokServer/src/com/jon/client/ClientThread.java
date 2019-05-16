package com.jon.client;

import com.jon.data.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientThread extends Thread {
    private Socket socket;
    private Protocol data;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;


    ClientThread(Socket socket) {
        this.socket = socket;
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                data = (Protocol) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (data.getStatus() == GameStatus.DEFAULT) {
                OmokClient.setPlayers(1);
            } else if (data.getStatus() == GameStatus.ALL_ENTRANCE) {
                System.out.println("test");
                OmokClient.setPlayers(2);
            }
        }
    }
}

