package net.hero.rogueb.bookofadventure;

import net.hero.rogueb.bookofadventure.domain.PlayerDomain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class BookOfAdventureController {
    private final BookOfAdventureService service;

    public BookOfAdventureController(BookOfAdventureService service) {
        this.service = service;
    }

    @GetMapping("/name/{userName}/exist")
    public Mono<Boolean> exist(@PathVariable("userName") String userName) {
        return this.service.exist(userName);
    }

    @PutMapping("/id/{userId}")
    public Mono<String> save(@PathVariable("userId") String userId, @RequestBody PlayerDomain playerDomain) {
        return Mono.just(playerDomain)
                .filter(it -> userId.equals(it.getId()))
                .flatMap(this.service::save);
    }

    @PostMapping("/name/{userName}")
    public Mono<String> create(@PathVariable("userName") String userName, @RequestBody Map<String, Object> currentStatus) {
        return this.service.create(userName, currentStatus);
    }

    @PostMapping("/id/{userId}/items")
    public Mono<String> changeObject(@PathVariable("userId") String playerId, @RequestBody List<String> objectIdList) {
        return this.service.changeObject(playerId, objectIdList);
    }

    @GetMapping("/id/{userId}")
    public Mono<PlayerDomain> getPlayer(@PathVariable("userId") String userId) {
        return this.service.getPlayerById(userId);
    }

    @GetMapping("/id/{userId}/items")
    public Flux<String> getItemList(@PathVariable("userId") String userId) {
        return this.service.getItemList(userId);
    }
}
