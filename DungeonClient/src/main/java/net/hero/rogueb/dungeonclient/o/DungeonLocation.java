package net.hero.rogueb.dungeonclient.o;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DungeonLocation(@JsonProperty("dungeonId") int dungeonId,
                              @JsonProperty("playerId") int playerId,
                              @JsonProperty("level") int level,
                              @JsonProperty("coordinate2D") Coordinate2D coordinate2D) {
}
