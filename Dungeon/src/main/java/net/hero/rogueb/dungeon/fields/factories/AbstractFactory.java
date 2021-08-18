package net.hero.rogueb.dungeon.fields.factories;

import net.hero.rogueb.dungeon.fields.Coordinate;
import net.hero.rogueb.dungeon.fields.Point;
import net.hero.rogueb.dungeon.base.o.PointType;

import java.util.List;

public interface AbstractFactory<T> {
    Point<T> createPoint(PointType pointType);
    Coordinate createCoordinate(List<Integer> coordinatePoints);
}
