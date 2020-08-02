package net.hero.rogueb.objects;

import net.hero.rogueb.math.Random;
import net.hero.rogueb.objects.mapper.CreatedObjectMapper;
import net.hero.rogueb.objects.mapper.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<Integer, Thing> getObjects(Collection<Integer> idList) {
        Map<Integer, Thing> result = new HashMap<>();
        for (var id : idList) {
            result.put(id, this.objectMapper.findById(id));
        }
        return result;
    }
}
