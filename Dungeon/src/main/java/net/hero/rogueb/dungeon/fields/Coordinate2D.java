package net.hero.rogueb.dungeon.fields;

public record Coordinate2D(int x, int y) implements Coordinate {
    @Override
    public Coordinate minus(Coordinate coordinate) {
        if (!(coordinate instanceof Coordinate2D c)) {
            throw new RuntimeException("It's only supported Coordinate2D.");
        }
        return new Coordinate2D(this.x - c.x(), this.y - c.y());
    }

    @Override
    public Coordinate plus(Coordinate coordinate) {
        if (!(coordinate instanceof Coordinate2D c)) {
            throw new RuntimeException("It's only supported Coordinate2D.");
        }
        return new Coordinate2D(this.x + c.x, this.y + c.y());
    }

    @Override
    public long area() {
        return this.x * (long) this.y;
    }

    @Override
    public int z() {
        throw new UnsupportedOperationException("Coordinate2D is not z axis.");
    }
}
