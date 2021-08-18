package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.dungeon.base.o.PointType;
import net.hero.rogueb.dungeon.fields.factories.AbstractFactory;
import net.hero.rogueb.dungeon.fields.factories.D2Factory;
import net.hero.rogueb.math.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Space {
    private final Coordinate2D maxSize;
    private final List<List<Point<String>>> spaces;
    private final Coordinate2D upStairs;
    private final Coordinate2D downStairs;
    private final AbstractFactory<String> factory;

    public Space(boolean isMax) {
        this.factory = new D2Factory();
        this.maxSize = new Coordinate2D(80, 40);
        this.spaces = new ArrayList<>();

        this.createRoom();
        this.upStairs = this.getRndPosition();
        this.spaces.get(this.upStairs.y()).set(this.upStairs.x(), this.factory.createPoint(PointType.UpStairs));
        Coordinate2D coordinate = null;
        while (isMax) {
            coordinate = this.getRndPosition();
            if (!this.upStairs.equals(coordinate)) {
                break;
            }
        }
        this.downStairs = coordinate;
        if(this.downStairs != null) {
            this.spaces.get(this.downStairs.y()).set(this.downStairs.x(), this.factory.createPoint(PointType.downStairs));
        }
    }

    public Space(Coordinate2D upstairs, Coordinate2D downStairs) {
        this.factory = new D2Factory();
        this.maxSize = new Coordinate2D(80, 40);
        this.spaces = new ArrayList<>();

        this.createRoom();
        this.upStairs = upstairs;
        this.spaces.get(this.upStairs.y()).set(this.upStairs.x(), this.factory.createPoint(PointType.UpStairs));
        this.downStairs = downStairs;
        if (this.downStairs != null) {
            this.spaces.get(this.downStairs.y()).set(this.downStairs.x(), this.factory.createPoint(PointType.downStairs));
        }
    }

    private void createRoom() {
        Room room = new Room(new Coordinate2D(1, 1), this.maxSize.minus(2, 2));
        this.spaces.add(Stream.generate(() ->
                this.factory.createPoint(PointType.Wall)).limit(this.maxSize.x()).collect(Collectors.toList()));
        for (int i = 1; i < maxSize.y() - 2; i++) {
            List<Point<String>> line = new ArrayList<>();
            line.add(this.factory.createPoint(PointType.Wall));
            line.addAll(room.getLine(i));
            line.add(this.factory.createPoint(PointType.Wall));
            this.spaces.add(line);
        }
        this.spaces.add(Stream.generate(() ->
                this.factory.createPoint(PointType.Wall)).limit(this.maxSize.x()).collect(Collectors.toList()));
    }

    public Coordinate2D getRndPosition() {
        return new Coordinate2D(1 + Random.rnd(this.maxSize.x() - 3), 1 + Random.rnd(this.maxSize.y() - 3));
    }

    public List<List<String>> getDisplay() {
        return this.spaces.stream().map(it -> it.stream().map(Point::display).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public Coordinate2D getDownStairs() {
        return downStairs;
    }

    public Coordinate2D getUpStairs() {
        return upStairs;
    }
}
