package net.hero.rogueb.dungeon.dto;

public class DungeonDto {
    private int id;
    private String name;
    private int maxLevel;
    private String namespace;
    private DungeonPlayerDto dungeonPlayerDto;

    public DungeonDto() {
    }

    public DungeonDto(String name, int maxLevel, String namespace) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.namespace = namespace;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public DungeonPlayerDto getDungeonPlayerDto() {
        return dungeonPlayerDto;
    }

    public void setDungeonPlayerDto(DungeonPlayerDto dungeonPlayerDto) {
        this.dungeonPlayerDto = dungeonPlayerDto;
    }
}
