package net.hero.rogueb.world;

import net.hero.rogueb.dungeonclient.DungeonServiceClient;
import net.hero.rogueb.dungeonclient.o.DungeonDto;
import net.hero.rogueb.world.mapper.ServiceMapper;
import net.hero.rogueb.world.o.DungeonInfo;
import net.hero.rogueb.world.o.ServiceInfo;
import org.springframework.stereotype.Service;

@Service
public class WorldService {
    private final DungeonServiceClient dungeonServiceClient;
    private final ServiceMapper serviceMapper;

    public WorldService(DungeonServiceClient dungeonServiceClient,
                        ServiceMapper serviceMapper) {
        this.dungeonServiceClient = dungeonServiceClient;
        this.serviceMapper = serviceMapper;
    }

    public DungeonInfo getStartDungeon() {
        final String dungeonName = "dungeon";
        DungeonDto dungeonDto = this.dungeonServiceClient.findByName(dungeonName);
        if (dungeonDto == null) {
            int id = this.dungeonServiceClient.save(dungeonName);
            return new DungeonInfo(id, dungeonName);
        }
        return new DungeonInfo(dungeonDto.id(), dungeonName);
    }

    public void registerService(ServiceInfo serviceInfo) {

    }

    public String getEndPoint(){
        return this.serviceMapper.findByType(1);
    }
}
