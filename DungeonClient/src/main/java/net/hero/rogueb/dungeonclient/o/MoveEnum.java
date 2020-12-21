package net.hero.rogueb.dungeonclient.o;

public enum MoveEnum {
    Top(0, -1),
    Left(-1, 0),
    Down(0, 1),
    Right(1, 0),
    TopLeft(-1, -1),
    TopRight(1, -1),
    DownLeft(-1, 1),
    DownRight(1, 1),
    ;

    private final int x;
    private final int y;

    MoveEnum(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
