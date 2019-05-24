package kr.ac.ajou.omok;

interface Protocol {
    byte GAMESTATUS = 0;
    byte READY_0 = 1;
    byte READY_1 = 2;
    byte DICE_0 = 3;
    //    byte DICE_1 = 4;
    byte COLOR_0 = 5;
    byte COLOR_1 = 6;
    byte READY_TO_RUN_0 = 7;
    //    byte READY_TO_RUN_1 = 8;
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
