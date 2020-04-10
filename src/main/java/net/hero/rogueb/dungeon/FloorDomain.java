package net.hero.rogueb.dungeon;

import net.hero.rogueb.fields.Coordinate2D;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FloorDomain {

    @Id
    private String id;

    private int dungeonId;

    private int userId;

    private int level;

    private Coordinate2D upStairs;

    private Coordinate2D downStairs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDungeonId() {
        return dungeonId;
    }

    public void setDungeonId(int dungeonId) {
        this.dungeonId = dungeonId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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
}
