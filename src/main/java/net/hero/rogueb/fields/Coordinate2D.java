package net.hero.rogueb.fields;

public class Coordinate2D implements  Coordinate {
    private int x;
    private int y;

    public Coordinate2D(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Coordinate2D coordinate2D){
            return x == coordinate2D.x && y == coordinate2D.y;
        }
        return false;
    }
}
