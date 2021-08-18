package net.hero.rogueb.dungeon.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DungeonPlayerDomain {
    @Id
    private String id;
    private String dungeonId;
    private String playerId;
    private int level;

    public DungeonPlayerDomain(){
    }

    public DungeonPlayerDomain(String dungeonId, String playerId, int level){
        this.dungeonId = dungeonId;
        this.playerId = playerId;
        this.level = level;
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

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
