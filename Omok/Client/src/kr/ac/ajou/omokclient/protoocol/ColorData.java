package kr.ac.ajou.omokclient.protoocol;

public class ColorData {
    private int colorOfPlayer0;
    private int colorOfPlayer1;

    public ColorData(int colorOfPlayer0, int colorOfPlayer1) {
        this.colorOfPlayer0 = colorOfPlayer0;
        this.colorOfPlayer1 = colorOfPlayer1;
    }

    public int getColorOfPlayer0() {
        return colorOfPlayer0;
    }

    public int getColorOfPlayer1() {
        return colorOfPlayer1;
    }
}