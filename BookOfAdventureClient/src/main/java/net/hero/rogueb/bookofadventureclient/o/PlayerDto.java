package net.hero.rogueb.bookofadventureclient.o;

import java.util.List;
import java.util.Map;

public class PlayerDto {
    private String id;
    private String name;
    private int level;
    private int exp;
    private int gold;
    private int totalPkCount;
    private int currentKillStreak;
    private int bounty;
    private String namespace;
    private Map<String, Object> currentStatus;
    private Map<String, Object> status;
    private Map<String, Object> location;
    private Map<String, String> equipment;
    private List<Integer> skillIds;
    private List<StatusEffectDto> statusEffects;

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

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Map<String, Object> getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Map<String, Object> currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Map<String, Object> getStatus() {
        return status;
    }

    public void setStatus(Map<String, Object> status) {
        this.status = status;
    }

    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotalPkCount() {
        return totalPkCount;
    }

    public void setTotalPkCount(int totalPkCount) {
        this.totalPkCount = totalPkCount;
    }

    public int getCurrentKillStreak() {
        return currentKillStreak;
    }

    public void setCurrentKillStreak(int currentKillStreak) {
        this.currentKillStreak = currentKillStreak;
    }

    public int getBounty() {
        return bounty;
    }

    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    public Map<String, String> getEquipment() {
        return equipment;
    }

    public void setEquipment(Map<String, String> equipment) {
        this.equipment = equipment;
    }

    public List<Integer> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Integer> skillIds) {
        this.skillIds = skillIds;
    }

    public List<StatusEffectDto> getStatusEffects() {
        return statusEffects;
    }

    public void setStatusEffects(List<StatusEffectDto> statusEffects) {
        this.statusEffects = statusEffects;
    }
}
