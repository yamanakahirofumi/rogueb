package net.hero.rogueb.world.o;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DungeonInfo(@JsonProperty("id") String id, @JsonProperty("name") String name) {
}
