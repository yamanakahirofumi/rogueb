package net.hero.rogueb.character;

import net.hero.rogueb.bag.Bag;

public class Human implements Player {
    private final String name;
    private final Bag bag;

    public Human(String name) {
        this.name = name;
        this.bag = new Bag();
    }

    @Override
    public String getName() {
        return name;
    }
}
