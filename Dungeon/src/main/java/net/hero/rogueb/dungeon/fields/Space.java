package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.math.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Space {
    private final Coordinate2D maxSize;
    private final List<Room> rooms;
    private final Coordinate2D upStairs;
    private final Coordinate2D downStairs;

    public Space(boolean isMax) {
        this.maxSize = new Coordinate2D(80, 40);
        this.rooms = new ArrayList<>();
        Room room = new Room(new Coordinate2D(1, 1), this.maxSize.minus(2, 2));
        this.rooms.add(room);
        this.upStairs = this.getRndPosition();
        Coordinate2D coordinate = null;
        while (isMax) {
            coordinate = this.getRndPosition();
            if (!this.upStairs.equals(coordinate)) {
                break;
            }
        }
        this.downStairs = coordinate;
    }

    public Space(Coordinate2D upstairs, Coordinate2D downStairs) {
        this.maxSize = new Coordinate2D(80, 40);
        this.rooms = new ArrayList<>();
        Room room = new Room(new Coordinate2D(1, 1), this.maxSize.minus(2, 2));
        this.rooms.add(room);
        this.upStairs = upstairs;
        this.downStairs = downStairs;
    }

    public Coordinate2D getRndPosition() {
        return new Coordinate2D(1 + Random.rnd(this.maxSize.x() - 3), 1 + Random.rnd(this.maxSize.y() - 3));
    }

    public List<List<String>> getDisplay() {
        List<List<String>> fields = Stream.generate(() ->
                Stream.generate(() -> "#").limit(this.maxSize.x()).collect(Collectors.toList())
        ).limit(this.maxSize.y()).collect(Collectors.toList());
        for (var r : this.rooms) {
            var display = r.getDisplay();
            for (var i = 0; i < r.size().y(); i++) {
                var xLine = fields.get(r.position().y() + i);
                var dxLine = display.get(i);
                for (var j = 0; j < r.size().x(); j++) {
                    xLine.set(r.position().x() + j, dxLine.get(j));
                }
            }
        }
        if (this.downStairs != null) {
            fields.get(this.downStairs.y()).set(this.downStairs.x(), ">");
        }
        fields.get(this.upStairs.y()).set(this.upStairs.x(), "<");
        return fields;
    }

    public Coordinate2D getDownStairs() {
        return downStairs;
    }

    public Coordinate2D getUpStairs() {
        return upStairs;
    }
}
