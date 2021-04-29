package net.hero.rogueb.dungeon.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DungeonPlayerDomain {
    @Id
    private String id;
    private String dungeonId;
    private int playerId;
    private int level;

    public DungeonPlayerDomain(){
    }

    public DungeonPlayerDomain(String dungeonId, int playerId, int level){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public void setDungeonId(String dungeonId) {
        this.dungeonId = dungeonId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
