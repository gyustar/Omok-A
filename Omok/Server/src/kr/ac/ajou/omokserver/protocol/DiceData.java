package kr.ac.ajou.omokserver.protocol;

public class DiceData {
    private int diceOfPlayer0;
    private int diceOfPlayer1;

    public DiceData(int diceOfPlayer0, int diceOfPlayer1) {
        this.diceOfPlayer0 = diceOfPlayer0;
        this.diceOfPlayer1 = diceOfPlayer1;
    }

    public int getDiceOfPlayer0() {
        return diceOfPlayer0;
    }

    public int getDiceOfPlayer1() {
        return diceOfPlayer1;
    }
}