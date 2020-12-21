package net.hero.rogueb.world.o;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DungeonInfo(@JsonProperty("id") int id, @JsonProperty("name") String name) {
}
