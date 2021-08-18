package net.hero.rogueb.dungeon;

import net.hero.rogueb.dungeon.domain.DungeonDomain;
import net.hero.rogueb.dungeon.fields.Coordinate2D;
import net.hero.rogueb.dungeon.fields.DisplayData;
import net.hero.rogueb.dungeon.fields.DungeonLocation;
import net.hero.rogueb.dungeon.fields.Gold;
import net.hero.rogueb.dungeon.base.o.ThingOverviewType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/dungeon/")
public class DungeonController {
    private final DungeonService dungeonService;

    public DungeonController(DungeonService dungeonService) {
        this.dungeonService = dungeonService;
    }

    @PostMapping("/{dungeonId}/go/{playerId}")
    public Mono<DungeonLocation> gotoDungeon(@PathVariable("dungeonId") String dungeonId,
                                             @PathVariable("playerId") String playerId) {
        return this.dungeonService.gotoDungeon(dungeonId, playerId);
    }

    @PostMapping("/{dungeonId}/move/{playerId}/{level}/{fromX}/{fromY}/{toX}/{toY}")
    public Mono<Coordinate2D> move(@PathVariable("dungeonId") String dungeonId,
                                   @PathVariable("playerId") String playerId,
                                   @PathVariable("level") int level,
                                   @PathVariable("fromX") int fromX,
                                   @PathVariable("fromY") int fromY,
                                   @PathVariable("toX") int toX,
                                   @PathVariable("toY") int toY) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, fromX, fromY);
        return this.dungeonService.move(location, toX, toY);
    }

    @GetMapping("/{dungeonId}/what/{playerId}/{level}/{x}/{y}")
    public Mono<ThingOverviewType> whatIsOnMyFeet(@PathVariable("dungeonId") String dungeonId,
                                                  @PathVariable("playerId") String playerId,
                                                  @PathVariable("level") int level,
                                                  @PathVariable("x") int x,
                                                  @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.whatIsOnMyFeet(location);
    }

    @PostMapping("/{dungeonId}/upstairs/{playerId}/{level}/{x}/{y}")
    public Mono<DungeonLocation> upStairs(@PathVariable("dungeonId") String dungeonId,
                                          @PathVariable("playerId") String playerId,
                                          @PathVariable("level") int level,
                                          @PathVariable("x") int x,
                                          @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.upStairs(location);
    }

    @PostMapping("/{dungeonId}/downstairs/{playerId}/{level}/{x}/{y}")
    public Mono<DungeonLocation> downStairs(@PathVariable("dungeonId") String dungeonId,
                                            @PathVariable("playerId") String playerId,
                                            @PathVariable("level") int level,
                                            @PathVariable("x") int x,
                                            @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.downStairs(location);
    }

    @PostMapping("/{dungeonId}/pickup/gold/{playerId}/{level}/{x}/{y}")
    public Mono<Gold> pickUpGold(@PathVariable("dungeonId") String dungeonId,
                                 @PathVariable("playerId") String playerId,
                                 @PathVariable("level") int level,
                                 @PathVariable("x") int x,
                                 @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.pickUpGold(location);
    }

    @PostMapping("/{dungeonId}/pickup/object/{playerId}/{level}/{x}/{y}")
    public Mono<Integer> pickUpObject(@PathVariable("dungeonId") String dungeonId,
                                      @PathVariable("playerId") String playerId,
                                      @PathVariable("level") int level,
                                      @PathVariable("x") int x,
                                      @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.pickUpObject(location);
    }

    @GetMapping("/{dungeonId}/name")
    public Mono<String> getDungeonName(@PathVariable("dungeonId") String dungeonId) {
        return this.dungeonService.getDungeonName(dungeonId);
    }

    @GetMapping("/{dungeonId}/display/{playerId}/{level}/{x}/{y}")
    public Flux<DisplayData> displayData(@PathVariable("dungeonId") String dungeonId,
                                         @PathVariable("playerId") String playerId,
                                         @PathVariable("level") int level,
                                         @PathVariable("x") int x,
                                         @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.displayData(location);
    }

    @GetMapping("/name/{dungeonName}")
    public Mono<DungeonDomain> findByName(@PathVariable String dungeonName) {
        return this.dungeonService.findByName(dungeonName);
    }

    @PutMapping("/name/{dungeonName}")
    public Mono<String> save(@PathVariable("dungeonName") String dungeonName) {
        return this.dungeonService.save(dungeonName);
    }
}
