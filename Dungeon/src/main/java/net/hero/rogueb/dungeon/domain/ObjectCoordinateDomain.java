package net.hero.rogueb.dungeon.domain;

import net.hero.rogueb.dungeon.fields.Coordinate;

public class ObjectCoordinateDomain {
    private Coordinate position;
    private String objectId;

    public ObjectCoordinateDomain() {

    }

    public ObjectCoordinateDomain(Coordinate position, String objectId) {
        this.position = position;
        this.objectId = objectId;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
