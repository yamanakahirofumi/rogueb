package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.dungeon.base.o.PointType;
import net.hero.rogueb.dungeon.fields.factories.AbstractFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Space<T> {
    private final Coordinate maxSize;
    private final Coordinate startCoordinate;
    private final List<List<Tile<T>>> tiles;
    private final Optional<Coordinate> upStairs;
    private final Optional<Coordinate> downStairs;
    private final List<Coordinate> goldCoordinates;
    private final List<Coordinate> thingCoordinates;
    private final AbstractFactory<T> factory;

    public Space(boolean isDownStairs, boolean isUpStairs,
                 Coordinate startCoordinate, Coordinate maxSize,
                 int goldCount, int itemCount,
                 AbstractFactory<T> factory) {
        this.factory = factory;
        this.maxSize = maxSize;
        this.startCoordinate = startCoordinate;

        Room<T> room = new Room<>(this.startCoordinate.plus(this.factory.createCoordinate(List.of(2, 2))),
                this.maxSize.minus(this.factory.createCoordinate(List.of(4, 4))),
                isUpStairs, isDownStairs, factory);
        this.upStairs = Optional.ofNullable(room.upStairs());
        this.downStairs = Optional.ofNullable(room.downStairs());
        this.tiles = this.createTiles(room);
        this.goldCoordinates = new LinkedList<>();
        while (goldCount <= this.goldCoordinates.size()) {
            Coordinate c = room.getRndPosition();
            if (!this.goldCoordinates.contains(c)) {
                this.goldCoordinates.add(c);
            }
        }
        this.thingCoordinates = new LinkedList<>();
        while (itemCount <= this.thingCoordinates.size()) {
            Coordinate c = room.getRndPosition();
            if (!this.goldCoordinates.contains(c) && !this.thingCoordinates.contains(c)) {
                this.thingCoordinates.add(c);
            }
        }
    }

    private List<List<Tile<T>>> createTiles(Room<T> room) {
        List<List<Tile<T>>> lists = Stream.generate(
                        () -> Stream.generate(
                                () -> this.factory.createPoint(PointType.Wall)).limit(this.maxSize.x()).toList())
                .limit(this.maxSize.y()).toList();
        for (int i = 1; i < maxSize.y() - 2; i++) {
            List<Tile<T>> line = new ArrayList<>();
            line.add(this.factory.createPoint(PointType.Wall));
            line.addAll(room.getLine(i));
            line.add(this.factory.createPoint(PointType.Wall));
            this.tiles.add(line);
        }
        return lists;
    }

    public List<List<T>> getDisplay() {
        return this.tiles.stream().map(it -> it.stream().map(Tile::display).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public Optional<Coordinate> getDownStairs() {
        return this.downStairs;
    }

    public Optional<Coordinate> getUpStairs() {
        return this.upStairs;
    }

    public List<Coordinate> getGoldCoordinates() {
        return this.goldCoordinates;
    }

    public List<Coordinate> getThingCoordinates() {
        return this.thingCoordinates;
    }

    public List<List<Tile<T>>> getTiles() {
        return tiles;
    }
}
