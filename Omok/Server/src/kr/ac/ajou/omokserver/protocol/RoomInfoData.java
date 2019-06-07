package kr.ac.ajou.omokserver.protocol;

public class RoomInfoData {
//    public static final short CREATE = 30;
//    public static final short MODIFY = 31;
//
//    private short header;
    private int roomNumber;
    private int numOfPlayer;

    public RoomInfoData(int roomNumber, int numOfPlayer) {
//        this.header = header;
        this.roomNumber = roomNumber;
        this.numOfPlayer = numOfPlayer;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getNumOfPlayer() {
        return numOfPlayer;
    }

//    public short getHeader() {
//        return header;
//    }
}
