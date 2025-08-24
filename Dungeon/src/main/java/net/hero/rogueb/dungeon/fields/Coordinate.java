package net.hero.rogueb.dungeon.fields;

public interface Coordinate {
    int x();
    int y();
    int z();
    Coordinate minus(Coordinate coordinate);
    Coordinate plus(Coordinate coordinate);
    long area();
}
