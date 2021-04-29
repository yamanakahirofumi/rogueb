package net.hero.rogueb.objectclient.o;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThingSimple(@JsonProperty("id") int id,@JsonProperty("display") String display) {
}
