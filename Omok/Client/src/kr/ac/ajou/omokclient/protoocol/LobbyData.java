package kr.ac.ajou.omokclient.protoocol;

public class LobbyData {
    public static final short NUM_OF_PLAYER = 100;
    public static final short CREATE_ROOM = 101;
    public static final short ENTER_ROOM = 102;

    private short header;
    private int numOfPlayer;
    private int roomNumber;

    public LobbyData(short header, int value) {
        this.header = header;
        if (header == NUM_OF_PLAYER) {
            numOfPlayer = value;
        } else if (header == ENTER_ROOM) {
            roomNumber = value;
        }
    }

    public LobbyData(short header) {
        this.header = header;
    }

    public short getHeader() {
        return header;
    }

    public int getNumOfPlayer() {
        return numOfPlayer;
    }

    public int getRoomNumber() {
        return roomNumber;
    }
}
