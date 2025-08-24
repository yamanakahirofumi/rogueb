package net.hero.rogueb.dungeon.fields.factories;

import net.hero.rogueb.dungeon.base.o.PointType;
import net.hero.rogueb.dungeon.fields.Coordinate;
import net.hero.rogueb.dungeon.fields.Coordinate2D;
import net.hero.rogueb.dungeon.fields.Gold;
import net.hero.rogueb.dungeon.fields.PointType2DDisplay;
import net.hero.rogueb.dungeon.fields.Tile;
import net.hero.rogueb.dungeon.fields.Tile2D;
import net.hero.rogueb.objectclient.o.ThingSimple;

import java.util.List;

public class D2StringFactory implements AbstractFactory<String> {
    @Override
    public Tile<String> createPoint(PointType pointType) {
        return new Tile2D(PointType2DDisplay.getDisplay(pointType), pointType);
    }

    @Override
    public Coordinate createCoordinate(List<Integer> coordinatePoints) {
        if (coordinatePoints.size() != 2) {
            throw new IllegalArgumentException("List size is not 2.");
        }
        return new Coordinate2D(coordinatePoints.get(0), coordinatePoints.get(1));
    }

    @Override
    public String getDisplay(ThingSimple thingSimple) {
        return thingSimple.display();
    }

    @Override
    public String getDisplay(Gold gold) {
        return gold.getDisplay();
    }

    @Override
    public String getPlayerDisplay() {
        return "@";
    }

    @Override
    public Tile<String> convertTile(Tile<String> s) {
        return s;
    }
}
