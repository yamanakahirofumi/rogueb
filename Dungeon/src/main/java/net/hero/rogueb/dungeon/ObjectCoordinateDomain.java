package net.hero.rogueb.dungeon;

import net.hero.rogueb.dungeon.fields.Coordinate2D;

public class ObjectCoordinateDomain {
    private Coordinate2D position;
    private int objectId;

    public ObjectCoordinateDomain(){

    }

    public ObjectCoordinateDomain(Coordinate2D position,int objectId){
        this.position = position;
        this.objectId = objectId;
    }

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
