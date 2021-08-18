package net.hero.rogueb.dungeon.fields;

public record Coordinate2D(int x, int y) implements Coordinate {
    public Coordinate2D minus(int minusX, int minusY) {
        return new Coordinate2D(this.x - minusX, this.y - minusY);
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate2D coordinate2D) {
            return x == coordinate2D.x && y == coordinate2D.y;
        }
        return false;
    }

    @Override
    public int z() {
        throw new UnsupportedOperationException("Coordinate2D is not z axis.");
    }
}
