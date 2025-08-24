package net.hero.rogueb.objects;

import net.hero.rogueb.objects.domain.TypeEnum;

public class ThingImpl implements Thing {
    private final String id;
    private final String name;
    private final TypeEnum type;
    private final String display;
    private final boolean many;

    public ThingImpl(String id, String name, TypeEnum type, String display, boolean many) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.display = display;
        this.many = many;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getDisplay() {
        return this.display;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public TypeEnum getType() {
        return this.type;
    }

    @Override
    public boolean isMany() {
        return this.many;
    }
}
