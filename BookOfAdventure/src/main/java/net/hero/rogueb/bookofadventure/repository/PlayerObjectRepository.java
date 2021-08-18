package net.hero.rogueb.bookofadventure.repository;

import net.hero.rogueb.bookofadventure.domain.PlayerObjectDomain;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PlayerObjectRepository extends ReactiveMongoRepository<PlayerObjectDomain, String> {
    Flux<PlayerObjectDomain> findByPlayerId(String playerId);
}
