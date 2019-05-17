package com.jon.client;

import com.jon.data.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ClientThread extends Thread {
    private byte[] data;
    private SocketChannel socketChannel;

    ClientThread(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        data = new byte[Protocol.SIZE.ordinal()];
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (socketChannel.isConnected()) System.out.println("isConnected");
                System.out.println("ㄱㄷ");
                ByteBuffer b = ByteBuffer.allocate(100);
                socketChannel.read(b);
                String s = Charset.forName("UTF-8").decode(b).toString();
                System.out.println(s);
                ByteBuffer buffer = ByteBuffer.allocate(data.length);
                socketChannel.read(buffer);
                System.out.println("받음");
                buffer.flip();
                data = buffer.array();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < data.length; ++i) {
                System.out.println(data[i]);
            }
            OmokClient.inputData(data);
        }
    }
}