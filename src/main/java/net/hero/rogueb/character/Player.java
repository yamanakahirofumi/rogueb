package net.hero.rogueb.character;

import net.hero.rogueb.fields.Location;
import net.hero.rogueb.fields.MoveEnum;

public interface Player {
    String getName();

    Location getLocation();

    void setLocation(Location location);

    boolean move(MoveEnum moveEnum);

    boolean pickUp();
}
