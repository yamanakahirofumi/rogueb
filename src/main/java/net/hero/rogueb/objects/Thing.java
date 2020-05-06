package net.hero.rogueb.objects;

public interface Thing {
    int getId();

    String getDisplay();

    String getName();

    int getType();

    boolean isMany();
}
