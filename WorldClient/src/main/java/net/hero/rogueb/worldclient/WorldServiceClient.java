package net.hero.rogueb.worldclient;

import net.hero.rogueb.worldclient.o.DungeonInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WorldServiceClient {
    private final WebClient webClient;

    public WorldServiceClient(String server) {
        this.webClient = WebClient.builder().baseUrl(server + "/api/world").build();
    }

    public Mono<DungeonInfo> getStartDungeon() {
        return this.webClient.get()
                .uri("/dungeon/init")
                .retrieve()
                .bodyToMono(DungeonInfo.class);
    }

}
