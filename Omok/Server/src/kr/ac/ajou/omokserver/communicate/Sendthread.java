package kr.ac.ajou.omokserver.communicate;

import com.google.gson.Gson;
import kr.ac.ajou.omokserver.protocol.Protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Sendthread extends Thread {
    private OutputStream[] oss;
    private DataOutputStream[] doss;
    private Gson gson;
    private byte[] data;

    Sendthread(OutputStream os, OutputStream os2, DataOutputStream dos, DataOutputStream dos2) {
        gson = new Gson();
        data = new byte[1024];

        oss = new OutputStream[2];
        oss[0] = os;
        oss[1] = os2;

        doss = new DataOutputStream[2];
        doss[0] = dos;
        doss[1] = dos2;
    }

    void broadcast(Protocol protocol) throws IOException {
        String json = gson.toJson(protocol);
        data = json.getBytes();
        int len = data.length;

        for (int i = 0; i < 2; ++i) {
            doss[i].writeInt(len);
            oss[i].write(data, 0, len);
        }
    }

}
