package net.hero.rogueb.dungeon.domain;

import net.hero.rogueb.dungeon.fields.Coordinate;
import net.hero.rogueb.dungeon.fields.Tile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class FloorDomain {

    @Id
    private String id;

    private String dungeonId;

    private String userId;

    private int level;

    private Coordinate upStairs;

    private Coordinate downStairs;

    private List<ObjectCoordinateDomain> thingList;

    private List<GoldCoordinateDomain> goldList;

    private List<List<Tile<String>>> tiles;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Coordinate getUpStairs() {
        return upStairs;
    }

    public void setUpStairs(Coordinate upStairs) {
        this.upStairs = upStairs;
    }

    public Coordinate getDownStairs() {
        return downStairs;
    }

    public void setDownStairs(Coordinate downStairs) {
        this.downStairs = downStairs;
    }

    public List<ObjectCoordinateDomain> getThingList() {
        return thingList;
    }

    public void setThingList(List<ObjectCoordinateDomain> thingList) {
        this.thingList = thingList;
    }

    public List<GoldCoordinateDomain> getGoldList() {
        return goldList;
    }

    public void setGoldList(List<GoldCoordinateDomain> goldList) {
        this.goldList = goldList;
    }

    public List<List<Tile<String>>> getTiles() {
        return tiles;
    }

    public void setTiles(List<List<Tile<String>>> tiles) {
        this.tiles = tiles;
    }
}
