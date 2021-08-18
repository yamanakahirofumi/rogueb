package net.hero.rogueb.bookofadventureclient;

import net.hero.rogueb.bookofadventureclient.o.PlayerDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class BookOfAdventureServiceClient {
    private final WebClient webClient;

    public BookOfAdventureServiceClient(String url) {
        this.webClient = WebClient.builder().baseUrl(url + "/api/user").build();
    }

    public Mono<String> save(PlayerDto playerDto) {
        return this.webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/id/{userId}").build(playerDto.getId()))
                .bodyValue(playerDto)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> changeObject(String playerId, List<Integer> objectIdList) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/id/{userId}/items").build(playerId))
                .bodyValue(objectIdList)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<PlayerDto> getPlayer(String id) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/id/{userId}").build(id))
                .retrieve()
                .bodyToMono(PlayerDto.class);
    }

    public Flux<Integer> getItemList(String playerId) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/id/{userId}/items").build(playerId))
                .retrieve()
                .bodyToFlux(Integer.class);
    }

    public Mono<Boolean> exist(String userName) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/name/{userName}/exist").build(userName))
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<String> create(String userName, Map<String, Object> playerInfo) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/name/{userName}").build(userName))
                .body(playerInfo, Map.class)
                .retrieve()
                .bodyToMono(String.class);
    }
}
