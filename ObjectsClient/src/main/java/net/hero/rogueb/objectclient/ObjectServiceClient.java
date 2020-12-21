package net.hero.rogueb.objectclient;

import net.hero.rogueb.objectclient.o.ThingInfo;
import net.hero.rogueb.objectclient.o.ThingSimple;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ObjectServiceClient {
    private final WebClient webClient;

    public ObjectServiceClient(String server) {
        this.webClient = WebClient.builder().baseUrl(server + "/api/objects").build();
    }

    public Mono<Map<Integer, ThingSimple>> getObjects(Collection<Integer> idList) {
        return this.webClient.post()
                .uri("/list")
                .bodyValue(idList)
                .retrieve()
                .bodyToFlux(ThingSimple.class)
                .collectMap(ThingSimple::id, it -> it);
    }

    public Mono<ThingInfo> getObjectInfo(int id) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{id}").build(id))
                .retrieve()
                .bodyToMono(ThingInfo.class);
    }

    public List<ThingSimple> createObjects(int itemCreateCount) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/create/count/{count}").build(itemCreateCount))
                .bodyValue(itemCreateCount)
                .retrieve()
                .bodyToFlux(ThingSimple.class)
                .collectList()
                .block();
    }
}
