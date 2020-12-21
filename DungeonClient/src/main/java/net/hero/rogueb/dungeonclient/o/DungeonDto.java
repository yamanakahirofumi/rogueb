package net.hero.rogueb.dungeonclient.o;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DungeonDto(@JsonProperty("id") int id,
                         @JsonProperty("name") String name,
                         @JsonProperty("namespace") String namespace) {
}
