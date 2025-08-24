package net.hero.rogueb.objects;

import net.hero.rogueb.objects.domain.TypeEnum;

public interface Thing {
    String getId();

    String getDisplay();

    String getName();

    TypeEnum getType();

    boolean isMany();
}
