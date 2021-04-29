package net.hero.rogueb.world;

import net.hero.rogueb.world.o.DungeonInfo;
import net.hero.rogueb.world.o.ServiceInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/world")
public class WorldController {
    private final WorldService worldService;

    public WorldController(WorldService worldService) {
        this.worldService = worldService;
    }

    @GetMapping("/dungeon/init")
    public Mono<DungeonInfo> getStartDungeon() {
        return this.worldService.getStartDungeon();
    }

    @PostMapping("/service")
    public void registerService(@RequestBody ServiceInfo serviceInfo){
        this.worldService.registerService(serviceInfo);
    }
}
