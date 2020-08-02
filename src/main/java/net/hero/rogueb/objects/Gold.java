package net.hero.rogueb.objects;

public class Gold implements Thing {
    private int gold;

    public Gold(int gold) {
        this.gold = gold;
    }

    public int getGold() {
        return this.gold;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public String getDisplay() {
        return "$";
    }

    @Override
    public String getName() {
        return "gold";
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public boolean isMany() {
        return true;
    }
}
