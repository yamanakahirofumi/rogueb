package net.hero.rogueb.objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/objects")
public class ObjectController {
    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @GetMapping("/{id}")
    public Thing getObjectInfo(@PathVariable("id") int id){
        return this.objectService.getObjectInfo(id);
    }

    @PostMapping("/list")
    public List<Thing> getObjects(@RequestBody Collection<Integer> idList) {
        return this.objectService.getObjects(idList);
    }

    @PostMapping("/create/count/{count}")
    public List<Thing> createObjects(@PathVariable("count") int itemCreateCount) {
        return this.objectService.createObjects(itemCreateCount);
    }
}
