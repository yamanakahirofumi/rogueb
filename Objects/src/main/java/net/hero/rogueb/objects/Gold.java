package net.hero.rogueb.objects;

import net.hero.rogueb.objects.domain.TypeEnum;

public class Gold implements Thing {
    private final int gold;

    public Gold(int gold) {
        this.gold = gold;
    }

    public int getGold() {
        return this.gold;
    }

    @Override
    public String getId() {
        return "";
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
    public TypeEnum getType() {
        return TypeEnum.OTHER;
    }

    @Override
    public boolean isMany() {
        return true;
    }
}
