package net.hero.rogueb.dungeon.domain;

import net.hero.rogueb.dungeon.fields.Coordinate2D;

public class GoldCoordinateDomain {
    private Coordinate2D position;
    private int gold;

    public GoldCoordinateDomain() {

    }

    public GoldCoordinateDomain(Coordinate2D position, int gold) {
        this.position = position;
        this.gold = gold;
    }

    public Coordinate2D getPosition() {
        return position;
    }

    public void setPosition(Coordinate2D position) {
        this.position = position;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
