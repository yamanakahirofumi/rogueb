package net.hero.rogueb.dungeon;

import net.hero.rogueb.fields.Coordinate2D;

public class ObjectCoodinateDomain {
    private Coordinate2D position;
    private int objectId;

    public Coordinate2D getPosition() {
        return position;
    }

    public void setPosition(Coordinate2D position) {
        this.position = position;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }
}
