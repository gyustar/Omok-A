package kr.ac.ajou.omokserver.protocol;

public class RoomInfoData {
    private int roomNumber;
    private int numOfPlayer;

    public RoomInfoData(int roomNumber, int numOfPlayer) {
        this.roomNumber = roomNumber;
        this.numOfPlayer = numOfPlayer;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getNumOfPlayer() {
        return numOfPlayer;
    }
}
