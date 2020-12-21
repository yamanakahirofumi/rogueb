package net.hero.rogueb.dungeon.repositories;

import net.hero.rogueb.dungeon.FloorDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FloorRepository extends MongoRepository<FloorDomain, String> {

    List<FloorDomain> findByDungeonIdAndLevelAndUserId(int dungeonId,int level, int userId);
}
