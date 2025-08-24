package net.hero.rogueb.dungeon.fields.factories;

import net.hero.rogueb.dungeon.fields.Coordinate;
import net.hero.rogueb.dungeon.fields.Gold;
import net.hero.rogueb.dungeon.fields.Tile;
import net.hero.rogueb.dungeon.base.o.PointType;
import net.hero.rogueb.objectclient.o.ThingSimple;

import java.util.List;

public interface AbstractFactory<T> {
    Tile<T> createPoint(PointType pointType);
    Coordinate createCoordinate(List<Integer> coordinatePoints);
    T getDisplay(ThingSimple thingSimple);
    T getDisplay(Gold gold);
    T getPlayerDisplay();
    Tile<T> convertTile(Tile<String> s);
}
