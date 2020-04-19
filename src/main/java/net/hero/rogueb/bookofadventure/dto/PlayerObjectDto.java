package net.hero.rogueb.bookofadventure.dto;

public class PlayerObjectDto {
    private int id;
    private int playerId;
    private int objectId;

    public PlayerObjectDto(){
    }

    public PlayerObjectDto(int playerId, int objectId){
        this.playerId = playerId;
        this.objectId = objectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }
}
