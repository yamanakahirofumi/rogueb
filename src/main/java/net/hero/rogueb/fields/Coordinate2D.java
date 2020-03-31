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
        //TODO: java14のinstanceof
        if(obj instanceof Coordinate2D){
            Coordinate2D c = (Coordinate2D)obj;
            return x == c.x && y == c.y;
        }

        return false;
    }
}
