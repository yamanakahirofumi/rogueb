package net.hero.rogueb.dungeon.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class DungeonDomain {
    @Id
    private String id;
    private String name;
    private String adminId;
    private int entryFee;
    private int maxLevel;
    private String rank;
    private long dungeonExp;
    private int itemSeed;
    private int monsterSeed;
    private int roomCountSeed;
    private String namespace;
    private boolean isIntrusionEnabled;
    private List<EnvironmentalEffect> environmentalEffects;
    private long lastActivityDate;
    private DeathPenaltyDomain deathPenalty;
    private ClearConditionDomain clearCondition;
    private ClearRewardDomain clearReward;

    public DungeonDomain() {
    }

    public DungeonDomain(String name, int maxLevel, String namespace, int itemSeed, int monsterSeed, int roomCountSeed) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.namespace = namespace;
        this.itemSeed = itemSeed;
        this.monsterSeed = monsterSeed;
        this.roomCountSeed = roomCountSeed;
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

    public int getMonsterSeed() {
        return monsterSeed;
    }

    public void setMonsterSeed(int monsterSeed) {
        this.monsterSeed = monsterSeed;
    }

    public int getRoomCountSeed() {
        return roomCountSeed;
    }

    public void setRoomCountSeed(int roomCountSeed) {
        this.roomCountSeed = roomCountSeed;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public long getDungeonExp() {
        return dungeonExp;
    }

    public void setDungeonExp(long dungeonExp) {
        this.dungeonExp = dungeonExp;
    }

    public boolean isIntrusionEnabled() {
        return isIntrusionEnabled;
    }

    public void setIntrusionEnabled(boolean intrusionEnabled) {
        this.isIntrusionEnabled = intrusionEnabled;
    }

    public List<EnvironmentalEffect> getEnvironmentalEffects() {
        return environmentalEffects;
    }

    public void setEnvironmentalEffects(List<EnvironmentalEffect> environmentalEffects) {
        this.environmentalEffects = environmentalEffects;
    }

    public long getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(long lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public DeathPenaltyDomain getDeathPenalty() {
        return deathPenalty;
    }

    public void setDeathPenalty(DeathPenaltyDomain deathPenalty) {
        this.deathPenalty = deathPenalty;
    }

    public ClearConditionDomain getClearCondition() {
        return clearCondition;
    }

    public void setClearCondition(ClearConditionDomain clearCondition) {
        this.clearCondition = clearCondition;
    }

    public ClearRewardDomain getClearReward() {
        return clearReward;
    }

    public void setClearReward(ClearRewardDomain clearReward) {
        this.clearReward = clearReward;
    }
}
