package net.hero.rogueb.bookofadventureclient;

import net.hero.rogueb.bookofadventureclient.o.PlayerDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class BookOfAdventureServiceClient {
    private final WebClient webClient;

    public BookOfAdventureServiceClient(String url) {
        this.webClient = WebClient.builder().baseUrl(url + "/api/user").build();
    }

    public Mono<Void> save(PlayerDto playerDto) {
        return this.webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/{userId}").build(playerDto.getId()))
                .bodyValue(playerDto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> changeObject(int playerId, List<Integer> objectIdList) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("{userId}/items").build(playerId))
                .bodyValue(objectIdList)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<PlayerDto> getPlayer(int id) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{userId}").build(id))
                .retrieve()
                .bodyToMono(PlayerDto.class);
    }

    public Mono<List<Integer>> getItemList(int playerId) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{userId}/items").build(playerId))
                .retrieve()
                .bodyToFlux(Integer.class)
                .collectList();
    }

    public Mono<Boolean> exist(String userName) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/name/{userName}/exist").build(userName))
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Integer> create(String userName) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/name/{userName}").build(userName))
                .retrieve()
                .bodyToMono(Integer.class);
    }
}
