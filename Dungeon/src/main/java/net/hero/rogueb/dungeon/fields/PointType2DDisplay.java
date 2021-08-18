package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.dungeon.base.o.PointType;

import java.util.Arrays;

public enum PointType2DDisplay {
    Wall("#", PointType.Wall),
    UpStairs("<", PointType.UpStairs),
    downStairs(">", PointType.downStairs),
    floor(".", PointType.floor);

    final String display;
    final PointType pointType;

    PointType2DDisplay(String display, PointType pointType){
        this.display = display;
        this.pointType = pointType;
    }

    public static String getDisplay(PointType p){
        //noinspection OptionalGetWithoutIsPresent
        return Arrays.stream(PointType2DDisplay.values())
                .filter(it -> it.pointType == p)
                .findFirst().get().display;
    }

    }
