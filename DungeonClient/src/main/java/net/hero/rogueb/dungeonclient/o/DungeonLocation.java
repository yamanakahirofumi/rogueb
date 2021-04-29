package net.hero.rogueb.dungeonclient.o;

public record DungeonLocation(String dungeonId,
                              int playerId,
                              int level,
                              Coordinate2D coordinate2D) {
}
