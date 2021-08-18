package net.hero.rogueb.dungeon.repositories;

import net.hero.rogueb.dungeon.domain.FloorDomain;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface FloorRepository extends ReactiveMongoRepository<FloorDomain, String> {

    Flux<FloorDomain> findByDungeonIdAndLevelAndUserId(String dungeonId, int level, String userId);
}
