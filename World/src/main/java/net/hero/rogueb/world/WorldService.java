package net.hero.rogueb.world;

import net.hero.rogueb.dungeonclient.DungeonServiceClient;
import net.hero.rogueb.world.mapper.ServiceMapper;
import net.hero.rogueb.world.o.DungeonInfo;
import net.hero.rogueb.world.o.ServiceInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class WorldService {
    private final DungeonServiceClient dungeonServiceClient;
    private final ServiceMapper serviceMapper;

    public WorldService(DungeonServiceClient dungeonServiceClient,
                        ServiceMapper serviceMapper) {
        this.dungeonServiceClient = dungeonServiceClient;
        this.serviceMapper = serviceMapper;
    }

    public Mono<DungeonInfo> getStartDungeon() {
        final String dungeonName = "dungeon";
        return this.dungeonServiceClient.findByName(dungeonName)
                .flatMap(dungeonDto -> {
                    if(dungeonDto.id() == null){
                        return Mono.empty();
                    }
                    return Mono.just(dungeonDto.id());
                })
                .switchIfEmpty(this.dungeonServiceClient.save(dungeonName))
                .map(id -> new DungeonInfo(id, dungeonName));
    }

    public void registerService(ServiceInfo serviceInfo) {
    }

    public String getEndPoint() {
        return this.serviceMapper.findByType(1);
    }
}
