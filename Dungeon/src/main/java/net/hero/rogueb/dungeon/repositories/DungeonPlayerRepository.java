package net.hero.rogueb.dungeon.repositories;

import net.hero.rogueb.dungeon.domain.DungeonPlayerDomain;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface DungeonPlayerRepository extends ReactiveMongoRepository<DungeonPlayerDomain, String> {
    Mono<Void> deleteDungeonPlayerDomainsByDungeonIdAndPlayerId(String dungeonId, int playerId);
}
