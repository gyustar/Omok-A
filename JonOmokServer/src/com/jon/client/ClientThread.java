package com.jon.client;

import com.jon.data.*;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;
    private byte[] data;
    private OutputStream os;
    private InputStream is;

    ClientThread(Socket socket) {
        this.socket = socket;
        data = new byte[Protocol.SIZE.ordinal()];
        try {
            this.os = socket.getOutputStream();
            this.is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                int ret = is.read(data);
                if (ret == -1) {
                    throw new IOException();
                }
                System.out.println("received");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.DEFAULT.ordinal()) {
                System.out.println("1");
                OmokClient.setPlayers(1);
            } else if (data[Protocol.GAMESTATUS.ordinal()] == (byte) Protocol.ALL_ENTER.ordinal()) {
                System.out.println("2");
                OmokClient.setPlayers(2);
            }
        }
    }
}