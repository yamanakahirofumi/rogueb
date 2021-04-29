package net.hero.rogueb.dungeon.fields;

public class Gold {
    private final int gold;

    public Gold(int gold) {
        this.gold = gold;
    }

    public int getGold() {
        return this.gold;
    }

    public int getId() {
        return -1;
    }

    public String getDisplay() {
        return "$";
    }

    public String getName() {
        return "gold";
    }

    public int getType() {
        return 0;
    }

    public boolean isMany() {
        return true;
    }
}
