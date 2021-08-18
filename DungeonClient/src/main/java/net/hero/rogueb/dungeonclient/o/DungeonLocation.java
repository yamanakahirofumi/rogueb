package net.hero.rogueb.dungeonclient.o;

public record DungeonLocation(String dungeonId,
                              String playerId,
                              int level,
                              Coordinate2D coordinate2D) {
}
