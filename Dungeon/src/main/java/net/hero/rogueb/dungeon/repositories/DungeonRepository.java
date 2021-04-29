package net.hero.rogueb.dungeon.repositories;

import net.hero.rogueb.dungeon.domain.DungeonDomain;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface DungeonRepository extends ReactiveMongoRepository<DungeonDomain, String> {
    Flux<DungeonDomain> findByName(String name);
}
