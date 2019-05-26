import processing.core.PApplet;

class Stone implements GUI {
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
        if (color == BLACK) BlackColor(p);
        else if (color == WHITE) WhiteColor(p);

        int x = BLOCK * (2 + j);
        int y = BLOCK * (2 + i);
        p.ellipse(x, y, DIAMETER, DIAMETER);
    }

    boolean checkStone(int i, int j) {
        return this.i == i && this.j == j;
    }
}
