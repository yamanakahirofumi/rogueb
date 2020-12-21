package net.hero.rogueb.objects;

import net.hero.rogueb.math.Random;
import net.hero.rogueb.objects.mapper.CreatedObjectMapper;
import net.hero.rogueb.objects.mapper.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObjectService {
    private final ObjectMapper objectMapper;
    private final CreatedObjectMapper createdObjectMapper;

    public ObjectService(ObjectMapper objectMapper, CreatedObjectMapper createdObjectMapper) {
        this.objectMapper = objectMapper;
        this.createdObjectMapper = createdObjectMapper;
    }

    public List<Thing> createObjects(int count) {
        List<Thing> objectList = new ArrayList<>();
        List<Ring> ringList = this.objectMapper.findRing();
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
        return objectList;
    }

    public List<Thing> getObjects(Collection<Integer> idList) {
        return idList.stream().map(this.objectMapper::findById).collect(Collectors.toList());
    }

    public Thing getObjectInfo(int id){
        return this.objectMapper.findById(id);
    }
}
