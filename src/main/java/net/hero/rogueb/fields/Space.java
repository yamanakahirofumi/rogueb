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
        this.maxSize = new Coordinate2D(80, 80);
        this.rooms = new ArrayList<>();
        Room room = new Room(new Coordinate2D(1, 1), new Coordinate2D(78, 78));
        this.rooms.add(room);
        this.upStairs = this.getRndPosition();
        this.downStairs = this.getRndPosition();
    }

    public Coordinate2D getRndPosition() {
        return new Coordinate2D(1 + Random.rnd(this.maxSize.getX() - 3), 1 + Random.rnd(this.maxSize.getY() -3));
    }

    public List<List<String>> getDisplay() {
        List<List<String>> fields = Stream.generate(() ->
                Stream.generate(() -> "#").limit(this.maxSize.getX()).collect(Collectors.toList())
        ).limit(this.maxSize.getY()).collect(Collectors.toList());
        for (var r : this.rooms) {
            var display = r.getDisplay();
            for (var i = 0; i < ((Coordinate2D) r.getSize()).getX(); i++) {
                var xLine = fields.get(((Coordinate2D) r.getPosition()).getX() + i);
                var dxLine = display.get(i);
                for (var j = 0; j < ((Coordinate2D) r.getSize()).getY(); j++) {
                    xLine.set(((Coordinate2D) r.getPosition()).getY() + j, dxLine.get(j));
                }
            }
        }
        fields.get(this.downStairs.getX()).set(this.downStairs.getY(), "<");
        fields.get(this.upStairs.getX()).set(this.upStairs.getY(), ">");
        return fields;
    }

    public Coordinate2D getDownStairs() {
        return downStairs;
    }

    public Coordinate2D getUpStairs() {
        return upStairs;
    }
}
