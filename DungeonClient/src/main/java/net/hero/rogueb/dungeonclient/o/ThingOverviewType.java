package net.hero.rogueb.dungeonclient.o;

public enum ThingOverviewType {
    None(0),
    Gold(1),
    Object(2);

    int type;

    ThingOverviewType(int type){
        this.type = type;
    }
}
