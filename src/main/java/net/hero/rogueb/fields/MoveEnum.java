package net.hero.rogueb.fields;

public enum MoveEnum {
    Top(-1,0),
    Left(0,-1),
    Down(1,0),
    Right(0,1),
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
