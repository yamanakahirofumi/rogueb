package net.hero.rogueb.object;

public class Ring implements Thing {
    private final String name;

    public Ring(String name){
        this.name = name;
    }

    @Override
    public String getDisplay() {
        return "=";
    }

    @Override
    public String getName() {
        return this.name;
    }
}
