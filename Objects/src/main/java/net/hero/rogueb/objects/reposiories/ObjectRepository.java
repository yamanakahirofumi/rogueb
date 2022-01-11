package net.hero.rogueb.objects.reposiories;

import net.hero.rogueb.objects.domain.RingDomain;
import net.hero.rogueb.objects.domain.TypeEnum;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ObjectRepository extends ReactiveMongoRepository<RingDomain, String> {
    Flux<RingDomain> findAllByType(TypeEnum type);
}
