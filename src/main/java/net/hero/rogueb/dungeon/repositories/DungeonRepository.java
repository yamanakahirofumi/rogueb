package net.hero.rogueb.dungeon.repositories;

import net.hero.rogueb.dungeon.dto.DungeonDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DungeonRepository extends MongoRepository<DungeonDto, String> {

}
