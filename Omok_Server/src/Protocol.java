interface Protocol {
    byte GAMESTATUS = 0;
    byte READY_0 = 1;
    byte READY_1 = 2;
    byte DICE_0 = 3;
    byte DICE_1 = 4;
    byte COLOR_0 = 5;
    byte COLOR_1 = 6;
    byte STONE_I = 7;
    byte STONE_J = 8;
    byte STONE_C = 9;
    byte TURN = 10;
    byte WINNER = 11;
    byte SIZE = 12;
    byte DEFAULT = 13;
    byte ALL_ENTER = 14;
    byte ALL_READY = 15;
    byte RUNNING = 16;
    byte END = 17;
}
