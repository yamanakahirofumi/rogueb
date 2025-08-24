package net.hero.rogueb.dungeon.fields;

import java.util.List;

public record DisplayData<T>(Coordinate position, List<T> data) {
}
