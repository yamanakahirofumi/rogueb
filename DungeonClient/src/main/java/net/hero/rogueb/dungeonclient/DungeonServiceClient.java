package net.hero.rogueb.dungeonclient;

import net.hero.rogueb.dungeon.base.o.ThingOverviewType;
import net.hero.rogueb.dungeonclient.o.Coordinate2D;
import net.hero.rogueb.dungeonclient.o.DisplayData;
import net.hero.rogueb.dungeonclient.o.DungeonDto;
import net.hero.rogueb.dungeonclient.o.DungeonLocation;
import net.hero.rogueb.dungeonclient.o.Gold;
import net.hero.rogueb.dungeonclient.o.MoveEnum;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

public class DungeonServiceClient {
    private final WebClient webClient;

    public DungeonServiceClient(String server) {
        this.webClient = WebClient.builder().baseUrl(server + "/api/dungeon").build();
    }

    public Mono<DungeonLocation> gotoDungeon(String dungeonId, String playerId) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/go/{playerId}").build(dungeonId, playerId))
                .retrieve()
                .bodyToMono(DungeonLocation.class);
    }

    public Mono<Coordinate2D> move(DungeonLocation dungeonLocation, MoveEnum moveEnum) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/{dungeonId}/move/{playerId}/{level}/{fromX}/{fromY}/{toX}/{toY}")
                        .build(dungeonLocation.dungeonId(), dungeonLocation.playerId(), dungeonLocation.level(),
                                dungeonLocation.coordinate2D().x(), dungeonLocation.coordinate2D().y(),
                                dungeonLocation.coordinate2D().x() + moveEnum.getX(),
                                dungeonLocation.coordinate2D().y() + moveEnum.getY()))
                .retrieve()
                .bodyToMono(Coordinate2D.class);
    }

    public Mono<ThingOverviewType> whatIsOnMyFeet(DungeonLocation dungeonLocation) {
        return this.webClient.get()
                .uri(uriBuilder -> this.defaultBuild(uriBuilder, "/{dungeonId}/what/{playerId}/{level}/{x}/{y}", dungeonLocation))
                .retrieve()
                .bodyToMono(ThingOverviewType.class);
    }

    public Mono<DungeonLocation> upStairs(DungeonLocation dungeonLocation) {
        return this.webClient.post()
                .uri(uriBuilder -> this.defaultBuild(uriBuilder,"/{dungeonId}/upstairs/{playerId}/{level}/{x}/{y}", dungeonLocation))
                .retrieve()
                .bodyToMono(DungeonLocation.class);
    }

    public Mono<DungeonLocation> downStairs(DungeonLocation dungeonLocation) {
        return this.webClient.post()
                .uri(uriBuilder -> this.defaultBuild(uriBuilder, "/{dungeonId}/downstairs/{playerId}/{level}/{x}/{y}", dungeonLocation))
                .retrieve()
                .bodyToMono(DungeonLocation.class);
    }

    public Mono<Gold> pickUpGold(DungeonLocation dungeonLocation) {
        return this.webClient.post()
                .uri(uriBuilder -> this.defaultBuild(uriBuilder, "/{dungeonId}/pickup/gold/{playerId}/{level}/{x}/{y}", dungeonLocation))
                .retrieve()
                .bodyToMono(Gold.class);
    }

    public Mono<Integer> pickUpObject(DungeonLocation dungeonLocation) {
        return this.webClient.post()
                .uri(uriBuilder -> this.defaultBuild(uriBuilder,"/{dungeonId}/pickup/object/{playerId}/{level}/{x}/{y}", dungeonLocation))
                .retrieve()
                .bodyToMono(Integer.class);
    }

    public Mono<String> getDungeonName(String dungeonId) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/name").build(dungeonId))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Flux<DisplayData> displayData(DungeonLocation dungeonLocation) {
        return this.webClient.get()
                .uri(uriBuilder -> this.defaultBuild(uriBuilder, "/{dungeonId}/display/{playerId}/{level}/{x}/{y}", dungeonLocation))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(DisplayData.class);
    }

    public Mono<DungeonDto> findByName(String dungeonName) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/name/{dungeonName}").build(dungeonName))
                .retrieve()
                .bodyToMono(DungeonDto.class)
                .log();
    }

    public Mono<String> save(String dungeonName) {
        return this.webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/name/{dungeonName}").build(dungeonName))
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("None");
    }

    private URI defaultBuild(UriBuilder uriBuilder, String path, DungeonLocation dungeonLocation){
        return uriBuilder.path(path).build(dungeonLocation.dungeonId(), dungeonLocation.playerId(),
                dungeonLocation.level(), dungeonLocation.coordinate2D().x(), dungeonLocation.coordinate2D().y());
    }
}
