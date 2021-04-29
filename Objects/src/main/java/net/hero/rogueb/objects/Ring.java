package net.hero.rogueb.objects;

public class Ring implements Thing {
    private final int id;
    private final String name;

    public Ring(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getDisplay() {
        return "=";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public boolean isMany() {
        return false;
    }
}
