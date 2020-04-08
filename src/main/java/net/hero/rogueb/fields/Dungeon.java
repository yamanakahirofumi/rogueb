package net.hero.rogueb.fields;

import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.dto.DungeonDto;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
    /**
     * 名前
     */
    private String name;
    /**
     * 最大階層
     */
    private int maxLevel;

    private List<Floor> floorList;

    public Dungeon(String name) {
        this.name = name;
        this.maxLevel = 1;
        this.floorList = new ArrayList<>();
    }

    public Dungeon(DungeonDto dungeonDto) {
        this.name = dungeonDto.getName();
        this.maxLevel = dungeonDto.getMaxLevel();
        this.floorList = new ArrayList<>();
        if (dungeonDto.getDungeonPlayerDto() != null) {
            for (var i = 0; i < dungeonDto.getDungeonPlayerDto().getLevel(); i++) {
                this.floorList.add(new Floor(i + 1));
            }
        }
    }

    public void createFloor(int level) {
        this.floorList.add(new Floor(level));
    }

    public Coordinate2D down(int level, PlayerDto playerDto) {
        return this.floorList.get(level - 1).enterFromUpStairs(playerDto);
    }

    public Coordinate2D move(PlayerDto playerDto){
        return this.floorList.get(playerDto.getLocationDto().getLevel() -1).move(playerDto);
    }

    public List<List<String>> getFields() {
        if (this.floorList.size() == 0) {
            return new ArrayList<>();
        }
        return this.floorList.get(0).getFields();
    }
}
