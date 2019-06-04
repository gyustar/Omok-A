import processing.core.PApplet;

public class Stone implements GUI {
    private final int i;
    private final int j;
    private final int color;

    Stone(int i, int j, int color) {
        this.i = i;
        this.j = j;
        this.color = color;
    }

    @Override
    public void display(PApplet p) {
        if (color == Settings.BLACK) fillBlack(p);
        else if (color == Settings.WHITE) fillWhite(p);

        int x = Settings.BLOCK * (2 + j);
        int y = Settings.BLOCK * (2 + i);
        p.ellipse(x, y, Settings.DIAMETER, Settings.DIAMETER);
    }

    boolean checkStone(int i, int j) {
        return this.i == i && this.j == j;
    }
}