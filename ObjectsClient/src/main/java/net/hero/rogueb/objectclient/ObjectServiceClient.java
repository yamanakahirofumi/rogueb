package net.hero.rogueb.objectclient;

import net.hero.rogueb.objectclient.o.ThingInfo;
import net.hero.rogueb.objectclient.o.ThingSimple;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

public class ObjectServiceClient {
    private final WebClient webClient;

    public ObjectServiceClient(String server) {
        this.webClient = WebClient.builder().baseUrl(server + "/api/objects").build();
    }

    public Mono<Map<String, ThingSimple>> getObjects(Collection<String> idList) {
        return this.webClient.post()
                .uri("/list")
                .bodyValue(idList)
                .retrieve()
                .bodyToFlux(ThingSimple.class)
                .collectMap(ThingSimple::instanceId, it -> it);
    }

    public Mono<ThingInfo> getObjectInfo(String id) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/instance/{id}").build(id))
                .retrieve()
                .bodyToMono(ThingInfo.class);
    }

    public Flux<ThingSimple> createObjects(int itemCreateCount, String description) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/create/count/{count}").build(itemCreateCount))
                .bodyValue(description)
                .retrieve()
                .bodyToFlux(ThingSimple.class);
    }

    public Mono<ThingSimple> addHistory(String id, String description) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/instance/{id}").build(id))
                .bodyValue(description)
                .retrieve()
                .bodyToMono(ThingSimple.class);
    }
}
