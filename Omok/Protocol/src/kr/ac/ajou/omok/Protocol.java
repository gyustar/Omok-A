package kr.ac.ajou.omok;

public interface Protocol {
    byte GAMESTATUS = 0;
    byte ENTER_0 = 1;
    byte ENTER_1 = 2;
    byte READY_0 = 3;
    byte READY_1 = 4;
    byte DICE_0 = 5;
    byte DICE_1 = 6;
    byte COLOR_0 = 7;
    byte COLOR_1 = 8;
    byte STONE_I = 9;
    byte STONE_J = 10;
    byte STONE_C = 11;
    byte TURN = 12;
    byte WINNER = 13;
    byte SIZE = 14;

    byte DEFAULT = 15;
    byte ALL_ENTER = 16;
    byte ALL_READY = 17;
    byte RUNNING = 18;
    byte END = 19;
}
