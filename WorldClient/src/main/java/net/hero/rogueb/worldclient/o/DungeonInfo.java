package net.hero.rogueb.worldclient.o;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DungeonInfo(@JsonProperty("id") int id, @JsonProperty("name") String name) {
}
