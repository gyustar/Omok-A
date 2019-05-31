package kr.ac.ajou.omokclient.protoocol;

public class StoneData {
    private int i;
    private int j;
    private int color;

    public StoneData(int i, int j, int color) {
        this.i = i;
        this.j = j;
        this.color = color;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getColor() {
        return color;
    }
}