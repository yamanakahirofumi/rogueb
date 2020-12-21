package net.hero.rogueb.objects;

public class ThingImpl implements Thing {
    private final int id;
    private final String name;
    private final int type;
    private final String display;
    private final boolean many;

    public ThingImpl(int id, String name, int type, String display, boolean many) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.display = display;
        this.many = many;
    }

    @Override
    public int getId() {
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
    public int getType() {
        return this.type;
    }

    @Override
    public boolean isMany() {
        return this.many;
    }
}
