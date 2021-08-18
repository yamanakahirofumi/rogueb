package net.hero.rogueb.objects;

import net.hero.rogueb.math.Random;
import net.hero.rogueb.objects.mapper.CreatedObjectMapper;
import net.hero.rogueb.objects.mapper.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ObjectService {
    private final ObjectMapper objectMapper;
    private final CreatedObjectMapper createdObjectMapper;

    public ObjectService(ObjectMapper objectMapper, CreatedObjectMapper createdObjectMapper) {
        this.objectMapper = objectMapper;
        this.createdObjectMapper = createdObjectMapper;
    }

    public Flux<Thing> createObjects(int count) {
        List<Ring> ringList = this.objectMapper.findRing();
        List<Thing> objectList = new ArrayList<>();
        for (var i = 0; i < count; i++) {
            Ring ring = ringList.get(Random.rnd(ringList.size()));
            objectList.add(ring);
        }
        for (Thing thing : objectList) {
            int id = thing.getId();
            if (this.createdObjectMapper.countById(id) > 0) {
                this.createdObjectMapper.updateCount(id);
            } else {
                this.createdObjectMapper.insertCount(id);
            }
        }
        return Flux.fromIterable(objectList);
    }

    public Flux<Thing> getObjects(Collection<Integer> idList) {
        return Flux.fromIterable(idList)
                .map(this.objectMapper::findById);
    }

    public Mono<Thing> getObjectInfo(int id){
        return Mono.just(this.objectMapper.findById(id));
    }
}
