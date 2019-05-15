package com.jon.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class OmokServer {
    public static void main(String[] args) {
        int n = 0;
        Thread[] clients = new ServerThread[2];

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket();
            InetAddress local = InetAddress.getLocalHost();
            serverSocket.bind(new InetSocketAddress(local.getHostAddress(), 5000));
            System.out.println("서버 열림: " + local.getHostName() + "\n");

            while (n!= 3) {
                Socket socket = serverSocket.accept();
                InetSocketAddress socketAddress =
                        (InetSocketAddress) socket.getRemoteSocketAddress();
                System.out.println(socketAddress.getHostName() + " 입장\n");
                clients[n] = new ServerThread(socket);
                clients[n++].start();
            }

            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}