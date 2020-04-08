package net.hero.rogueb.dungeon;

import net.hero.rogueb.fields.Coordinate2D;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DungeonDomain {

    @Id
    private String id;

    private int dungeonId;

    private int userId;

    private int level;

}
