package net.hero.rogueb.dungeon.fields;

public enum ThingOverviewType {
    None(0),
    Gold(1),
    Object(2);

    final int type;

    ThingOverviewType(int type){
        this.type = type;
    }
}
