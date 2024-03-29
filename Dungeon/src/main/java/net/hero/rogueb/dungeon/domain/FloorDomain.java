package net.hero.rogueb.dungeon.domain;

import net.hero.rogueb.dungeon.fields.Coordinate2D;
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

    private Coordinate2D upStairs;

    private Coordinate2D downStairs;

    private List<ObjectCoordinateDomain> thingList;

    private List<GoldCoordinateDomain> goldList;

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

    public Coordinate2D getUpStairs() {
        return upStairs;
    }

    public void setUpStairs(Coordinate2D upStairs) {
        this.upStairs = upStairs;
    }

    public Coordinate2D getDownStairs() {
        return downStairs;
    }

    public void setDownStairs(Coordinate2D downStairs) {
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
}
