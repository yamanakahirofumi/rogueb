package net.hero.rogueb.dungeon.domain;

import net.hero.rogueb.dungeon.fields.Coordinate;

public class GoldCoordinateDomain {
    private Coordinate position;
    private int gold;

    public GoldCoordinateDomain() {

    }

    public GoldCoordinateDomain(Coordinate position, int gold) {
        this.position = position;
        this.gold = gold;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
