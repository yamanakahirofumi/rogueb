package net.hero.rogueb.dungeon.domain;

import net.hero.rogueb.dungeon.fields.Coordinate2D;

public class ObjectCoordinateDomain {
    private Coordinate2D position;
    private String objectId;

    public ObjectCoordinateDomain(){

    }

    public ObjectCoordinateDomain(Coordinate2D position, String objectId){
        this.position = position;
        this.objectId = objectId;
    }

    public Coordinate2D getPosition() {
        return position;
    }

    public void setPosition(Coordinate2D position) {
        this.position = position;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
