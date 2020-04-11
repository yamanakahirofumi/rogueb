package net.hero.rogueb.fields;

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

    public Space() {
        this.maxSize = new Coordinate2D(80, 40);
        this.rooms = new ArrayList<>();
        Room room = new Room(new Coordinate2D(1, 1), this.maxSize.minus(2, 2));
        this.rooms.add(room);
        this.upStairs = this.getRndPosition();
        this.downStairs = this.getRndPosition();
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
        return new Coordinate2D(1 + Random.rnd(this.maxSize.getX() - 3), 1 + Random.rnd(this.maxSize.getY() - 3));
    }

    public List<List<String>> getDisplay() {
        List<List<String>> fields = Stream.generate(() ->
                Stream.generate(() -> "#").limit(this.maxSize.getX()).collect(Collectors.toList())
        ).limit(this.maxSize.getY()).collect(Collectors.toList());
        for (var r : this.rooms) {
            var display = r.getDisplay();
            for (var i = 0; i < r.size().getY(); i++) {
                var xLine = fields.get(r.position().getY() + i);
                var dxLine = display.get(i);
                for (var j = 0; j < r.size().getX(); j++) {
                    xLine.set(r.position().getX() + j, dxLine.get(j));
                }
            }
        }
        fields.get(this.downStairs.getY()).set(this.downStairs.getX(), "<");
        fields.get(this.upStairs.getY()).set(this.upStairs.getX(), ">");
        return fields;
    }

    public Coordinate2D getDownStairs() {
        return downStairs;
    }

    public Coordinate2D getUpStairs() {
        return upStairs;
    }
}
