package net.hero.rogueb.dungeonclient.o;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Coordinate2D(@JsonProperty("x") int x,@JsonProperty("y") int y) {
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
}
