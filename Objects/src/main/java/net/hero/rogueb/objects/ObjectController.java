package net.hero.rogueb.objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
@RequestMapping("/api/objects")
public class ObjectController {
    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @GetMapping("/instance/{id}")
    public Mono<ThingInstance> getObjectInfo(@PathVariable("id") String id) {
        return this.objectService.getObjectInfo(id);
    }

    @PostMapping("/list")
    public Flux<ThingInstance> getObjects(@RequestBody Collection<String> idList) {
        return this.objectService.getObjects(idList);
    }

    @PostMapping("/create/count/{count}")
    public Flux<ThingInstance> createObjects(@PathVariable("count") int itemCreateCount, @RequestBody String description) {
        return this.objectService.createObjects(itemCreateCount, description);
    }

    @PostMapping("/instance/{id}/")
    public Mono<ThingInstance> addHistory(@PathVariable("id") String id, @RequestBody String description) {
        return this.objectService.addHistory(id, description);
    }
}
