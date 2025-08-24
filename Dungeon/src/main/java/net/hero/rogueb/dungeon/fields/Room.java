package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.dungeon.base.o.PointType;
import net.hero.rogueb.dungeon.fields.factories.AbstractFactory;
import net.hero.rogueb.math.Random;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Room<T> {
    private final AbstractFactory<T> factory;
    private final Coordinate size;
    private final Coordinate position;
    private final Coordinate upStairs;
    private final Coordinate downStairs;

    public Room(Coordinate minPosition, Coordinate maxSize, boolean isUpStairs, boolean isDownStairs,
                AbstractFactory<T> factory) {
        this.factory = factory;
        this.size = maxSize;
        this.position = minPosition;
        if (isUpStairs) {
            this.upStairs = this.getRndPosition();
        } else {
            this.upStairs = null;
        }
        if (isDownStairs) {
            Coordinate coordinate = this.getRndPosition();
            while (coordinate.equals(this.upStairs)) {
                coordinate = this.getRndPosition();
            }
            this.downStairs = coordinate;
        } else {
            this.downStairs = null;
        }
    }

    public List<Tile<T>> getLine(int y) {
        return Stream.generate(() -> this.factory.createPoint(PointType.floor))
                .limit(this.size.x())
                .collect(Collectors.toList());
    }

    public Coordinate position() {
        return position;
    }

    public Coordinate size() {
        return this.size;
    }

    public Coordinate getRndPosition() {
        return this.factory.createCoordinate(
                List.of(this.position.x() + Random.rnd(this.size.x()), this.position.y() + Random.rnd(this.size.y())));
    }

    public Coordinate upStairs() {
        return upStairs;
    }

    public Coordinate downStairs() {
        return downStairs;
    }
}
