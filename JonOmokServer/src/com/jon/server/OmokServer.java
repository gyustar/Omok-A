package com.jon.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class OmokServer {
    public static void main(String[] args) {
        int n = 0;
        ServerThread[] clients = new ServerThread[2];

        ServerSocketChannel serverSocketChannel;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(true);
            InetAddress local = InetAddress.getLocalHost();
            serverSocketChannel.bind(new InetSocketAddress(local.getHostAddress(), 5000));
            System.out.println("서버 열림: " + local.getHostAddress() + "\n");

            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                InetSocketAddress socketAddress =
                        (InetSocketAddress) socketChannel.getRemoteAddress();
                System.out.println(socketAddress.getHostName() + " 입장\n");
                clients[n] = new ServerThread(socketChannel, clients);
                clients[n++].start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}