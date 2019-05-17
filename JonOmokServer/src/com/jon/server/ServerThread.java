package com.jon.server;

import com.jon.data.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ServerThread extends Thread {
    private static final Object MUTEX = new Object();
    private static int n = 0;
    private static byte[] data = new byte[Protocol.SIZE.ordinal()];
    private int id;
    private static SocketChannel[] scs = new SocketChannel[2];
    private SocketChannel socketChannel;
    private static ServerThread[] clients = new ServerThread[2];

    ServerThread(SocketChannel socketChannel, ServerThread[] cl) {
        synchronized (MUTEX) {
            clients = cl;
            clients[n] = this;
            this.id = n;
            scs[n++] = socketChannel;
            this.socketChannel = socketChannel;
            if (this.id == 0) {
                data[Protocol.ENTER_0.ordinal()] = 1;
                data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.DEFAULT.ordinal();
            } else if (this.id == 1) {
                data[Protocol.ENTER_1.ordinal()] = 1;
                data[Protocol.GAMESTATUS.ordinal()] = (byte) Protocol.ALL_ENTER.ordinal();
            }
        }
    }

//    private void broadcast() {
//        synchronized (MUTEX) {
//            for (int i = 0; i < n; ++i) {
//                try {
//                    ByteBuffer buffer = ByteBuffer.allocate(data.length);
//                    buffer.put(data);
//                    scs[i].write(buffer);
//                    System.out.println("보냄" + i);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private void broadcast() {
        synchronized (MUTEX) {
            for (int i = 0; i < n; ++i) {
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(data.length);
                    buffer.put(data);
                    clients[i].socketChannel.write(buffer);
//                    scs[i].write(buffer);
                    System.out.println("보냄" + i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void inputData() {
        synchronized (MUTEX) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(data.length);
                scs[id].read(buffer);
                buffer.flip();
                data = buffer.array();
                System.out.println("받음");
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