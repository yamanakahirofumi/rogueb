package net.hero.rogueb.services;

import net.hero.rogueb.bookofadventureclient.o.PlayerDto;
import net.hero.rogueb.bookofadventureclient.BookOfAdventureServiceClient;
import net.hero.rogueb.dungeonclient.DungeonServiceClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class FieldsService {
    private final BookOfAdventureServiceClient bookOfAdventureServiceClient;
    private final DungeonServiceClient dungeonServiceClient;

    public FieldsService(BookOfAdventureServiceClient bookOfAdventureServiceClient,
                         DungeonServiceClient dungeonServiceClient) {
        this.bookOfAdventureServiceClient = bookOfAdventureServiceClient;
        this.dungeonServiceClient = dungeonServiceClient;
    }

    public Mono<List<List<String>>> getFields(int userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .map(PlayerDto::getLocationDto)
                .flatMap(locationDto -> this.dungeonServiceClient.displayData(locationDto.getDungeonId(), userId, locationDto.getLevel(), locationDto.getX(), locationDto.getY()));
    }

    public Mono<Map<String, String>> getDungeonInfo(int userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .map(PlayerDto::getLocationDto)
                .flatMap(locationDto -> Mono.zip(Mono.just(locationDto.getLevel()), this.dungeonServiceClient.getDungeonName(locationDto.getDungeonId())))
            .map(tuple2 -> Map.of("level", String.valueOf(tuple2.getT1()),
                    "name", tuple2.getT2()));
    }
}
