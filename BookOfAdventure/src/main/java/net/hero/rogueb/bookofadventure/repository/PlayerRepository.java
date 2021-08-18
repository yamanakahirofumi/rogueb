package net.hero.rogueb.bookofadventure.repository;

import net.hero.rogueb.bookofadventure.domain.PlayerDomain;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepository extends ReactiveMongoRepository<PlayerDomain, String> {
    Flux<PlayerDomain> findByName(String name);
    Mono<Boolean> existsByName(String name);
}
