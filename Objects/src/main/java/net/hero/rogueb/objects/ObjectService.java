package net.hero.rogueb.objects;

import net.hero.rogueb.math.Random;
import net.hero.rogueb.objects.domain.ObjectHistoryDomain;
import net.hero.rogueb.objects.domain.TypeEnum;
import net.hero.rogueb.objects.reposiories.HistoryRepository;
import net.hero.rogueb.objects.reposiories.ObjectRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Service
public class ObjectService {
    private final ObjectRepository objectRepository;
    private final HistoryRepository historyRepository;

    public ObjectService(ObjectRepository objectRepository, HistoryRepository historyRepository) {
        this.objectRepository = objectRepository;
        this.historyRepository = historyRepository;
    }

    public Flux<ThingInstance> createObjects(int count, String description) {
        return this.objectRepository.findAllByType(TypeEnum.RING).collectList()
                .flatMapMany(it -> Flux.range(0, count).map(c -> it.get(Random.rnd(it.size()))))
                .flatMap(it -> this.recordObject(it, description))
                .map(ObjectHistoryDomain::changeInstance);
    }

    private Mono<ObjectHistoryDomain> recordObject(Thing thing, String description) {
        ObjectHistoryDomain objectHistoryDomain = new ObjectHistoryDomain(thing, description);
        return this.historyRepository.save(objectHistoryDomain)
                .doOnNext(it -> it.setParentId(it.getId()))
                .flatMap(this.historyRepository::save);
    }

    public Flux<ThingInstance> getObjects(Collection<String> idList) {
        return Flux.fromIterable(idList)
                .flatMap(this.historyRepository::findByParentIdOrderByCreateDateDesc)
                .map(ObjectHistoryDomain::changeInstance);
    }

    public Mono<ThingInstance> getObjectInfo(String id) {
        return this.historyRepository.findById(id)
                .map(ObjectHistoryDomain::changeInstance);
    }

    public Mono<ThingInstance> addHistory(String id, String description) {
        return this.historyRepository.findByParentIdOrderByCreateDateDesc(id)
                .map(it -> new ObjectHistoryDomain(it.getThing(), it.getParentId(), description))
                .flatMap(this.historyRepository::save)
                .map(ObjectHistoryDomain::changeInstance);
    }
}
