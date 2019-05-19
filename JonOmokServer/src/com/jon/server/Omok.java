package com.jon.server;

class Omok {
    private byte[][] stones;

    Omok() {
        stones = new byte[15][15];
    }

    void putStone(int i, int j, int color) {
        stones[i][j] = (byte) color;
    }

    boolean winCheck(int i, int j) {
        return checkHorizon(i, j) || checkVertical(i, j) ||
                checkDiagonalRU(i, j) || checkDiagonalRD(i, j);
    }

    private boolean checkHorizon(int i, int j) {
        int count = 1;
        for (int k = 1; k < 6; ++k) {
            try {
                if (stones[i][j] == stones[i][j + k]) count++;
                else break;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        for (int k = 1; k < 6; ++k) {
            try {
                if (stones[i][j] == stones[i][j - k]) count++;
                else break;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        return count == 5;
    }

    private boolean checkVertical(int i, int j) {
        int count = 1;
        for (int k = 1; k < 6; ++k) {
            try {
                if (stones[i][j] == stones[i + k][j]) count++;
                else break;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        for (int k = 1; k < 6; ++k) {
            try {
                if (stones[i][j] == stones[i - k][j]) count++;
                else break;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        return count == 5;
    }

    private boolean checkDiagonalRU(int i, int j) {
        int count = 1;
        for (int k = 1; k < 6; ++k) {
            try {
                if (stones[i][j] == stones[i - k][j + k]) count++;
                else break;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        for (int k = 1; k < 6; ++k) {
            try {
                if (stones[i][j] == stones[i + k][j - k]) count++;
                else break;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        return count == 5;
    }

    private boolean checkDiagonalRD(int i, int j) {
        int count = 1;
        for (int k = 1; k < 6; ++k) {
            try {
                if (stones[i][j] == stones[i + k][j + k]) count++;
                else break;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        for (int k = 1; k < 6; ++k) {
            try {
                if (stones[i][j] == stones[i - k][j - k]) count++;
                else break;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        return count == 5;
    }
}
