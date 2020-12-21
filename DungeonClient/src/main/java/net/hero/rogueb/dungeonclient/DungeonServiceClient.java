package net.hero.rogueb.dungeonclient;

import net.hero.rogueb.dungeonclient.o.Coordinate2D;
import net.hero.rogueb.dungeonclient.o.DungeonDto;
import net.hero.rogueb.dungeonclient.o.DungeonLocation;
import net.hero.rogueb.dungeonclient.o.Gold;
import net.hero.rogueb.dungeonclient.o.MoveEnum;
import net.hero.rogueb.dungeonclient.o.ThingOverviewType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class DungeonServiceClient {
    private final WebClient webClient;

    public DungeonServiceClient(String server) {
        this.webClient = WebClient.builder().baseUrl(server + "/api/dungeon").build();
    }

    public Mono<DungeonLocation> gotoDungeon(int dungeonId, int playerId) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/go/{playerId}").build(dungeonId, playerId))
                .retrieve()
                .bodyToMono(DungeonLocation.class);
    }

    public Mono<Coordinate2D> move(int dungeonId, int playerId, int level, int fromX, int fromY, MoveEnum moveEnum) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/{dungeonId}/move/{playerId}/{level}/{fromX}/{fromY}/{toX}/{toY}")
                        .build(dungeonId, playerId, level, fromX, fromY, fromX + moveEnum.getX(), fromY + moveEnum.getY()))
                .retrieve()
                .bodyToMono(Coordinate2D.class);
    }

    public Mono<ThingOverviewType> whatIsOnMyFeet(int dungeonId, int playerId, int level, int x, int y) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{dungeonId}/what/{playerId}/{level}/{x}/{y}")
                        .build(dungeonId, playerId, level, x, y))
                .retrieve()
                .bodyToMono(ThingOverviewType.class);
    }

    public Mono<DungeonLocation> upStairs(int dungeonId, int playerId, int level, int x, int y) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/upstairs/{playerId}/{level}/{x}/{y}")
                        .build(dungeonId, playerId, level, x, y))
                .retrieve()
                .bodyToMono(DungeonLocation.class);
    }

    public Mono<DungeonLocation> downStairs(int dungeonId, int playerId, int level, int x, int y) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/downstairs/{playerId}/{level}/{x}/{y}")
                        .build(dungeonId, playerId, level, x, y))
                .retrieve()
                .bodyToMono(DungeonLocation.class);
    }

    public Mono<Gold> pickUpGold(int dungeonId, int playerId, int level, int x, int y) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/pickup/gold/{playerId}/{level}/{x}/{y}")
                        .build(dungeonId, playerId, level, x, y))
                .retrieve()
                .bodyToMono(Gold.class);
    }

    public Mono<Integer> pickUpObject(int dungeonId, int playerId, int level, int x, int y) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/pickup/object/{playerId}/{level}/{x}/{y}")
                        .build(dungeonId, playerId, level, x, y))
                .retrieve()
                .bodyToMono(Integer.class);
    }

    public Mono<String> getDungeonName(int dungeonId) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/name").build(dungeonId))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<List<List<String>>> displayData(int dungeonId, int userId, int level, int x, int y) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{dungeonId}/display/{playerId}/{level}/{x}/{y}")
                        .build(dungeonId, userId, level, x, y))
                .retrieve()
                .bodyToMono(List.class)
                .map(list -> (List<List<String>>) list);
    }

    public DungeonDto findByName(String dungeonName) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/name/{dungeonName}").build(dungeonName))
                .retrieve()
                .bodyToMono(DungeonDto.class)
                .block();
    }

    public int save(String dungeonName) {
        return this.webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/name/{dungeonName}").build(dungeonName))
                .retrieve()
                .bodyToMono(Integer.class)
                .defaultIfEmpty(0)
                .block();
    }
}
