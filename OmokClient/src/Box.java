import processing.core.PApplet;

public class Box implements GUI {
    private static final int BOX_X = Settings.BLOCK;
    private static final int BOX_Y = Settings.BLOCK * 5;
    private static final int BOX_W = Settings.BOARD;
    private static final int BOX_H = Settings.BLOCK * 8;

    private String msg;

    Box(String msg) {
        this.msg = msg;
    }

    @Override
    public void display(PApplet p) {
        fillTransparent(p);
        p.rect(BOX_X, BOX_Y, BOX_W, BOX_H);
        fillBlack(p);
        p.textSize(Settings.TEXT_SIZE);
        p.textAlign(p.CENTER, p.CENTER);
        p.text(msg, Settings.BLOCK + Settings.BOARD / 2, Settings.BLOCK + Settings.BOARD / 2 - 7);
    }
}