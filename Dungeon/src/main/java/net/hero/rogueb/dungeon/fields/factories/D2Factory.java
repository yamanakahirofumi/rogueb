package net.hero.rogueb.dungeon.fields.factories;

import net.hero.rogueb.dungeon.fields.Coordinate;
import net.hero.rogueb.dungeon.fields.Coordinate2D;
import net.hero.rogueb.dungeon.fields.Point;
import net.hero.rogueb.dungeon.fields.Point2D;
import net.hero.rogueb.dungeon.base.o.PointType;
import net.hero.rogueb.dungeon.fields.PointType2DDisplay;

import java.util.List;

public class D2Factory implements AbstractFactory<String> {
    @Override
    public Point<String> createPoint(PointType pointType) {
        return new Point2D(PointType2DDisplay.getDisplay(pointType), pointType);
    }

    @Override
    public Coordinate createCoordinate(List<Integer> coordinatePoints) {
        if(coordinatePoints.size() != 2) {
            throw new IllegalArgumentException("List size is not 2.");
        }
        return new Coordinate2D(coordinatePoints.get(0), coordinatePoints.get(1));
    }
}
