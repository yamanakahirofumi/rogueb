package net.hero.rogueb.objects.reposiories;

import net.hero.rogueb.objects.domain.ObjectHistoryDomain;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface HistoryRepository extends ReactiveMongoRepository<ObjectHistoryDomain, String> {

    Mono<ObjectHistoryDomain> findByParentIdOrderByCreateDateDesc(String parentId);
}
