package kr.ac.ajou.omokclient.gui;

import processing.core.PApplet;

public class Box implements GUI {
    private static final int BOX_X = BLOCK;
    private static final int BOX_Y = BLOCK * 5;
    private static final int BOX_W = BOARD;
    private static final int BOX_H = BLOCK * 8;

    private String text;

    public Box(String text) {
        this.text = text;
    }

//    public Box(int winner) {
//        this.boxKind = WINBOX;
//        this.count = FRAME_SECOND * 6;
//        this.end = false;
//
//        this.winner = winner;
//    }
//
//    public Box(int dice, int color) {
//        this.boxKind = DICEBOX;
//        this.count = FRAME_SECOND * 11;
//        this.end = false;
//
//        this.dice = dice;
//        this.color = color;
//    }

    @Override
    public void display(PApplet p) {
        fillTransparent(p);
        p.rect(BOX_X, BOX_Y, BOX_W, BOX_H);
        fillBlack(p);
        p.textSize(TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text(text, BLOCK + BOARD / 2, BLOCK + BOARD / 2 - 7);
    }

//    private void drawDiceBox(PApplet p) {
//        if (count-- > 0) {
//            fillTransparent(p);
//            p.rect(BOX_X, BOX_Y, BOX_W, BOX_H);
//            if (count / FRAME_SECOND > 7) {
//                fillBlack(p);
//                p.textSize(TEXT_SIZE_BIG);
//                p.textAlign(p.CENTER, p.CENTER);
//                p.text(count / FRAME_SECOND - 7, BLOCK + BOARD / 2, BLOCK + BOARD / 2 - 7);
//            } else if (count / FRAME_SECOND > 2) {
//                fillWhite(p);
//                p.rect(BLOCK * 4, BLOCK * 7, BLOCK * 4, BLOCK * 4, BLOCK / 2);
//
//                fillBlack(p);
//                p.textSize(TEXT_SIZE);
//                p.textAlign(p.CENTER, p.CENTER);
//                p.text(dice, BLOCK * 6, BLOCK + BOARD / 2 - 4);
//
//                String s = "";
//                if (color == BLACK) s = "BLACK!";
//                else if (color == WHITE) s = "WHITE!";
//                p.text(s, BLOCK * 11, BLOCK + BOARD / 2 - 4);
//            } else {
//                fillBlack(p);
//                p.textSize(TEXT_SIZE_BIG);
//                p.textAlign(p.CENTER, p.CENTER);
//                p.text("START!", BLOCK + BOARD / 2, BLOCK + BOARD / 2 - 4);
//            }
//        } else if (count <= 0) end = true;
//    }
//
//    private void drawWinBox(PApplet p) {
//        if (count-- > 0) {
//            fillTransparent(p);
//            p.rect(BOX_X, BOX_Y, BOX_W, BOX_H);
//
//            fillBlack(p);
//            p.textSize(TEXT_SIZE_BIG);
//            p.textAlign(p.CENTER, p.CENTER);
//            String s = "Player" + winner + " Win!!";
//            p.text(s, BLOCK + BOARD / 2, BLOCK + BOARD / 2 - 7);
//        } else if (count <= 0) end = true;
//    }

//    boolean isDiceBoxEnd() {
//        return this.end && this.boxKind == DICEBOX;
//    }
//
//    boolean isWinBoxEnd() {
//        return this.end && this.boxKind == WINBOX;
//    }
}
