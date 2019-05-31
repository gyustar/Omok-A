package kr.ac.ajou.omokserver.communicate;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static Socket[] sockets = new Socket[2];

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket();
            InetAddress local = InetAddress.getLocalHost();
            serverSocket.bind(new InetSocketAddress(local.getHostAddress(), 5000));
            System.out.println("서버 열림: " + local.getHostAddress() + "\n");

            int n = 0;
            while (true) {
                Socket socket = serverSocket.accept();
                InetSocketAddress socketAddress =
                        (InetSocketAddress) socket.getRemoteSocketAddress();
                System.out.println(socketAddress.getHostName() + " 입장\n");

                sockets[n++] = socket;
                ServerThread thread = new ServerThread(socket);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
