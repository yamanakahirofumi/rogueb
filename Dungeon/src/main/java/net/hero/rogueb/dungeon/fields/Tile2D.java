package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.dungeon.base.o.PointType;

public record Tile2D(String display, PointType pointType) implements Tile<String> {
}
