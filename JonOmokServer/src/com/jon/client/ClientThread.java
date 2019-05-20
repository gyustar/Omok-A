package com.jon.client;

import com.jon.data.*;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private byte[] data;
    private Socket socket;

    ClientThread(Socket socket) {
        this.socket = socket;
        data = new byte[Protocol.SIZE.ordinal()];
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStream is = socket.getInputStream();
                int ret = is.read(data);
                if (ret == -1) throw new IOException();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            OmokClient.inputData(data);
        }
    }
}