package net.hero.rogueb.dungeon.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DungeonDomain {
    @Id
    private String id;
    private String name;
    private int maxLevel;
    private int itemSeed;
    private String namespace;

    public DungeonDomain() {
    }

    public DungeonDomain(String name, int maxLevel, String namespace, int itemSeed) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.namespace = namespace;
        this.itemSeed = itemSeed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getItemSeed() {
        return itemSeed;
    }

    public void setItemSeed(int itemSeed) {
        this.itemSeed = itemSeed;
    }
}
