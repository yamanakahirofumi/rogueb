package net.hero.rogueb.controllers;

import net.hero.rogueb.dungeonclient.o.MoveEnum;
import net.hero.rogueb.services.FieldsService;
import net.hero.rogueb.services.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FieldsController {

    private final FieldsService fieldsService;
    private final PlayerService playerService;

    FieldsController(FieldsService fieldsService, PlayerService playerService) {
        this.fieldsService = fieldsService;
        this.playerService = playerService;
    }

    @GetMapping("/user/name/{userName}/exist")
    public Mono<Boolean> existPlayer(@PathVariable("userName") String userName){
        return this.playerService.existPlayer(userName);
    }

    @PostMapping("/user/name/{userName}")
    public Mono<Integer> create(@PathVariable("userName") String userName){
        return this.playerService.create(userName);
    }

    @PostMapping("/player/{userId}")
    public Mono<Map<String, String>> gotoDungeon(@PathVariable("userId") int userId) {
        return this.playerService.gotoDungeon(userId);
    }

    @PutMapping("/player/{userId}/command/top")
    public Mono<Map<String, Boolean>> top(@PathVariable("userId") int userId) {
        return this.playerService.move(userId, MoveEnum.Top);
    }

    @PutMapping("/player/{userId}/command/down")
    public Mono<Map<String, Boolean>> down(@PathVariable("userId") int userId) {
        return this.playerService.move(userId, MoveEnum.Down);
    }

    @PutMapping("/player/{userId}/command/right")
    public Mono<Map<String, Boolean>> right(@PathVariable("userId") int userId) {
        return this.playerService.move(userId, MoveEnum.Right);
    }

    @PutMapping("/player/{userId}/command/left")
    public Mono<Map<String, Boolean>> left(@PathVariable("userId") int userId) {
        return this.playerService.move(userId, MoveEnum.Left);
    }

    @PutMapping("/player/{userId}/command/top-right")
    public Mono<Map<String, Boolean>> topRight(@PathVariable("userId") int userId) {
        return this.playerService.move(userId, MoveEnum.TopRight);
    }

    @PutMapping("/player/{userId}/command/top-left")
    public Mono<Map<String, Boolean>> topLeft(@PathVariable("userId") int userId) {
        return this.playerService.move(userId, MoveEnum.TopLeft);
    }

    @PutMapping("/player/{userId}/command/down-right")
    public Mono<Map<String, Boolean>> downRight(@PathVariable("userId") int userId) {
        return this.playerService.move(userId, MoveEnum.DownRight);
    }

    @PutMapping("/player/{userId}/command/down-left")
    public Mono<Map<String, Boolean>> downLeft(@PathVariable("userId") int userId) {
        return this.playerService.move(userId, MoveEnum.DownLeft);
    }

    @PutMapping("/player/{userId}/command/pickup")
    public Mono<Map<String, Object>> pickUp(@PathVariable("userId") int userId) {
        return this.playerService.pickup(userId);
    }

    @PutMapping("/player/{userId}/command/downStairs")
    public Mono<Map<String, Boolean>> downStairs(@PathVariable("userId") int userId) {
        return this.playerService.downStairs(userId);
    }

    @PutMapping("/player/{userId}/command/upStairs")
    public Mono<Map<String, Boolean>> upStairs(@PathVariable("userId") int userId) {
        return this.playerService.upStairs(userId);
    }

    @GetMapping("/fields/{userId}")
    public Mono<List<List<String>>> getFields(@PathVariable("userId") int userId) {
        return this.fieldsService.getFields(userId);
    }

    @GetMapping("fields/{userId}/info")
    public Mono<Map<String, String>> getDungeonInfo(@PathVariable("userId") int userId) {
        return this.fieldsService.getDungeonInfo(userId);
    }
}
