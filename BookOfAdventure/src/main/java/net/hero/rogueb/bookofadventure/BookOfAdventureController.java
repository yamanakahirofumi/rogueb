package net.hero.rogueb.bookofadventure;

import net.hero.rogueb.bookofadventure.dto.PlayerDto;
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

@RestController
@RequestMapping("/api/user")
public class BookOfAdventureController {
    private final BookOfAdventureService service;

    public BookOfAdventureController(BookOfAdventureService service) {
        this.service = service;
    }

    @GetMapping("/name/{userName}/exist")
    public Mono<Boolean> exist(@PathVariable("userName") String userName) {
        return Mono.just(this.service.exist(userName));
    }

    @PutMapping("/{userId}")
    public void save(@PathVariable("userId") int userId, @RequestBody PlayerDto playerDto) {
        if (userId == playerDto.getId()) {
            this.service.save(playerDto);
        }
    }

    @PostMapping("/name/{userName}")
    public Mono<Integer> create(@PathVariable("userName") String userName) {
        return Mono.just(this.service.create(userName));
    }

    @PostMapping("/{userId}/items")
    public void changeObject(@PathVariable("userId") int playerId, @RequestBody List<Integer> objectIdList) {
        this.service.changeObject(playerId, objectIdList);
    }

    @GetMapping("/{userId}")
    public Mono<PlayerDto> getPlayer(@PathVariable("userId") int userId) {
        return Mono.just(this.service.getPlayer(userId));
    }

    @GetMapping("/{userId}/items")
    public Flux<Integer> getItemList(@PathVariable("userId") int userId) {
        return Flux.fromIterable(this.service.getItemList(userId));
    }
}
