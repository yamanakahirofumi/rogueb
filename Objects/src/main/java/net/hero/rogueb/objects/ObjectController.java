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

    @GetMapping("/{id}")
    public Mono<Thing> getObjectInfo(@PathVariable("id") int id){
        return this.objectService.getObjectInfo(id);
    }

    @PostMapping("/list")
    public Flux<Thing> getObjects(@RequestBody Collection<Integer> idList) {
        return this.objectService.getObjects(idList);
    }

    @PostMapping("/create/count/{count}")
    public Flux<Thing> createObjects(@PathVariable("count") int itemCreateCount) {
        return this.objectService.createObjects(itemCreateCount);
    }
}
