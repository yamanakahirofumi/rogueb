package net.hero.rogueb.services;

import net.hero.rogueb.bookofadventureclient.BookOfAdventureServiceClient;
import net.hero.rogueb.bookofadventureclient.o.PlayerDto;
import net.hero.rogueb.converts.Convert;
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

    public Flux<DisplayData> getFields(String userId) {
        return Flux.merge(Flux.just(1), Flux.interval(Duration.ofSeconds(20)))
                .flatMap(i -> this.getFieldsNow(userId));
    }

    public Flux<DisplayData> getFieldsNow(String userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .map(Convert::playDto2DungeonLocation)
                .flatMapMany(this.dungeonServiceClient::displayData);
    }

    public Mono<Map<String, String>> getDungeonInfo(String userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .map(PlayerDto::getLocation)
                .flatMap(locationDto -> Mono.zip(Mono.just(locationDto.get("level")), this.dungeonServiceClient.getDungeonName(locationDto.get("dungeonId").toString())))
                .map(tuple2 -> Map.of("level", String.valueOf(tuple2.getT1()), "name", tuple2.getT2()));
    }

}
