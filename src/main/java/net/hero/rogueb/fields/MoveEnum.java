package net.hero.rogueb.fields;

public enum MoveEnum {
    Top(0,-1),
    Left(-1,0),
    Down(0,1),
    Right(1,0),
    ;

    private final int x;
    private final int y;
    MoveEnum(int x, int y){
        this.x =x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
