import processing.core.PApplet;

public class WinBox implements GUI {
    private static final int BOX_X = BLOCK;
    private static final int BOX_Y = BLOCK * 5;
    private static final int BOX_W = BOARD;
    private static final int BOX_H = BLOCK * 8;
    private static final int FRAME_SECOND = 30;

    private final int boxKind;
    private int count;
    private boolean end;

    private int dice;
    private int color;

    private int winner;

    WinBox(int dice, int color) {
        this.boxKind = DICEBOX;
        this.count = FRAME_SECOND * 11;
        this.end = false;

        this.dice = dice;
        this.color = color;
    }

    WinBox(int winner) {
        this.boxKind = WINBOX;
        this.count = FRAME_SECOND * 6;
        this.end = false;

        this.winner = winner;
    }

    @Override
    public void display(PApplet p) {
        if (boxKind == DICEBOX) drawDiceBox(p);
        else if (boxKind == WINBOX) drawWinBox(p);
    }

    private void drawDiceBox(PApplet p) {
        if (count-- > 0) {
            WinBoxColor(p);
            p.rect(BOX_X, BOX_Y, BOX_W, BOX_H);
            if (count / FRAME_SECOND > 7) {
                BlackColor(p);
                p.textSize(TEXT_SIZE_BIG);
                p.textAlign(p.CENTER, p.CENTER);
                p.text(count / FRAME_SECOND - 7, BLOCK + BOARD / 2f, BLOCK + BOARD / 2f - 7);
            } else if (count / FRAME_SECOND > 2) {
                WhiteColor(p);
                p.rect(BLOCK * 4, BLOCK * 7, BLOCK * 4, BLOCK * 4, BLOCK / 2f);

                BlackColor(p);
                p.textSize(TEXT_SIZE);
                p.textAlign(p.CENTER, p.CENTER);
                p.text(dice, BLOCK * 6, BLOCK + BOARD / 2f - 4);

                String s = "";
                if (color == BLACK) s = "BLACK!";
                else if (color == WHITE) s = "WHITE!";
                p.text(s, BLOCK * 11, BLOCK + BOARD / 2f - 4);
            } else {
                BlackColor(p);
                p.textSize(TEXT_SIZE_BIG);
                p.textAlign(p.CENTER, p.CENTER);
                p.text("START!", BLOCK + BOARD / 2f, BLOCK + BOARD / 2f - 4);
            }
        } else if (count <= 0) end = true;
    }

    private void drawWinBox(PApplet p) {
        if (count-- > 0) {
            WinBoxColor(p);
            p.rect(BOX_X, BOX_Y, BOX_W, BOX_H);

            BlackColor(p);
            p.textSize(TEXT_SIZE_BIG);
            p.textAlign(p.CENTER, p.CENTER);
            String s = "Player" + winner + " Win!!";
            p.text(s, BLOCK + BOARD / 2f, BLOCK + BOARD / 2f - 7);
        } else if (count <= 0) end = true;
    }

    boolean isEnd() {
        return this.end;
    }

}
