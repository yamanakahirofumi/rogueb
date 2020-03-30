package net.hero.rogueb.fields;

import net.hero.rogueb.character.Player;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {

    /**
     * TODO:暫定
     */
    private static Dungeon instance;

    /**
     * 名前
     */
    private String name;
    /**
     * 最大階層
     */
    private int maxLevel;

    private List<Floor> floorList;

    /**
     * TODO:暫定
     *
     * @return
     */
    public static Dungeon getInstance() {
        if (instance == null) {
            instance = new Dungeon();
        }
        return instance;
    }

    private Dungeon() {
        this.name = "dungeon";
        this.maxLevel = 1;
        this.floorList = new ArrayList<>();
    }

    public void setPlayer(Player player) {
        player.getLocation().dungeon = this;
        this.floorList.add(new Floor(this.floorList.size() + 1, player));
    }

    public List<List<String>> getFields() {
        if (this.floorList.size() == 0) {
            return new ArrayList<>();
        }
        return this.floorList.get(0).getFields();
    }
}
