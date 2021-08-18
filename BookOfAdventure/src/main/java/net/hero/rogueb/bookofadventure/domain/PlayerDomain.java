package net.hero.rogueb.bookofadventure.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
public class PlayerDomain {
    @Id
    private String id;
    private String name;
    private int exp;
    private int gold;
    private String namespace;
    private Map<String, Object> currentStatus;
    private Map<String, Object> status;
    private Map<String, Object> location;

    public PlayerDomain() {

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

    public void setCurrentStatus(Map<String,Object> currentStatus) {
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

    public void setLocation(Map<String,Object> location) {
        this.location = location;
    }
}
