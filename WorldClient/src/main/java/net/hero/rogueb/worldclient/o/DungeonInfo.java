package net.hero.rogueb.worldclient.o;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DungeonInfo(@JsonProperty("id") String id, @JsonProperty("name") String name) {
}
