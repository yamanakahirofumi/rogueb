package net.hero.rogueb.objects.domain;

import java.util.Arrays;

public enum TypeEnum {
    ARMOR(1),
    RING(2),
    SCROLL(3),
    WEAPON(4),
    STICK(5),
    OTHER(0);

    private final int id;

    TypeEnum(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return this.id;
    }

    public static TypeEnum convert(String name) {
        return Arrays.stream(TypeEnum.values())
                .filter(it -> it.name().equals(name))
                .findFirst()
                .orElse(OTHER);
    }

}
