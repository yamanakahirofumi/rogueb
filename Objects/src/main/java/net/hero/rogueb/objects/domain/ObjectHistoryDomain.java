package net.hero.rogueb.objects.domain;

import net.hero.rogueb.objects.Thing;
import net.hero.rogueb.objects.ThingInstance;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Document
public class ObjectHistoryDomain {

    @Id
    private String id;
    private Thing thing;
    private String parentId;
    private String description;
    private LocalDateTime createDate;
    private ZoneId zoneId;

    public ObjectHistoryDomain() {
    }

    public ObjectHistoryDomain(Thing thing, String description) {
        this.thing = thing;
        this.parentId = "";
        this.description = description;
        this.zoneId = ZoneOffset.UTC.normalized();
        this.createDate = LocalDateTime.now(this.zoneId);
    }

    public ObjectHistoryDomain(Thing thing, String parentId, String description) {
        this.thing = thing;
        this.parentId = parentId;
        this.description = description;
        this.zoneId = ZoneOffset.UTC.normalized();
        this.createDate = LocalDateTime.now(this.zoneId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public ThingInstance changeInstance() {
        return new ThingInstance(this.parentId, this.thing.getId(), this.thing.getName(), this.thing.getDisplay());
    }
}
