package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.dungeon.base.o.PointType;
import net.hero.rogueb.dungeon.fields.factories.AbstractFactory;
import net.hero.rogueb.dungeon.fields.factories.D2Factory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record Room(Coordinate2D position, Coordinate2D size) {
    public List<Point<String>> getLine(int y) {
        AbstractFactory<String> factory = new D2Factory();
        return Stream.generate(() -> factory.createPoint(PointType.floor))
                .limit(this.size.x())
                .collect(Collectors.toList());
    }
}
