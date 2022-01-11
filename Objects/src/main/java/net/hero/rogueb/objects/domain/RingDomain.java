package net.hero.rogueb.objects.domain;

import net.hero.rogueb.objects.Thing;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document
public class RingDomain implements Thing {
    @Id
    private String id;
    private String name;
    @Field(targetType = FieldType.STRING)
    private TypeEnum type;

    public RingDomain() {
    }

    public RingDomain(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplay() {
        return "=";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isMany() {
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }
}
