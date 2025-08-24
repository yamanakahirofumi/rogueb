package net.hero.rogueb.bookofadventure.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class PlayerObjectDomain {
    @Id
    private String id;
    private String playerId;
    private List<String> objectIdList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public List<String> getObjectIdList() {
        return objectIdList;
    }

    public void setObjectIdList(List<String> objectIdList) {
        this.objectIdList = objectIdList;
    }
}
