package net.hero.rogueb.services;

import net.hero.rogueb.bookofadventureclient.o.PlayerDto;
import net.hero.rogueb.bookofadventureclient.BookOfAdventureServiceClient;
import net.hero.rogueb.dungeonclient.DungeonServiceClient;
import net.hero.rogueb.dungeonclient.o.DisplayData;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

    public Flux<DisplayData> getFields(int userId) {
        return Flux.merge(Flux.just(1), Flux.interval(Duration.ofSeconds(20)))
                .flatMap(i ->this.getFieldsNow(userId));
    }

    public Flux<DisplayData> getFieldsNow(int userId){
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .map(PlayerDto::getLocationDto)
                .flatMapMany(locationDto -> this.dungeonServiceClient.displayData(locationDto.getDungeonId(), userId, locationDto.getLevel(), locationDto.getX(), locationDto.getY()));
    }

    public Mono<Map<String, String>> getDungeonInfo(int userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .map(PlayerDto::getLocationDto)
                .flatMap(locationDto -> Mono.zip(Mono.just(locationDto.getLevel()), this.dungeonServiceClient.getDungeonName(locationDto.getDungeonId())))
            .map(tuple2 -> Map.of("level", String.valueOf(tuple2.getT1()),
                    "name", tuple2.getT2()));
    }
}
