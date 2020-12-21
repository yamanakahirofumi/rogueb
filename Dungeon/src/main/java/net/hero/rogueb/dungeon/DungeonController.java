package net.hero.rogueb.dungeon;

import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.dungeon.fields.Coordinate2D;
import net.hero.rogueb.dungeon.fields.DungeonLocation;
import net.hero.rogueb.dungeon.fields.Gold;
import net.hero.rogueb.dungeon.fields.ThingOverviewType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dungeon/")
public class DungeonController {
    private final DungeonService dungeonService;

    public DungeonController(DungeonService dungeonService) {
        this.dungeonService = dungeonService;
    }

    @PostMapping("/{dungeonId}/go/{playerId}")
    public DungeonLocation gotoDungeon(@PathVariable("dungeonId") int dungeonId,
                                       @PathVariable("playerId") int playerId) {
        return this.dungeonService.gotoDungeon(dungeonId, playerId);
    }

    @PostMapping("/{dungeonId}/move/{playerId}/{level}/{fromX}/{fromY}/{toX}/{toY}")
    public Coordinate2D move(@PathVariable("dungeonId") int dungeonId,
                             @PathVariable("playerId") int playerId,
                             @PathVariable("level") int level,
                             @PathVariable("fromX") int fromX,
                             @PathVariable("fromY") int fromY,
                             @PathVariable("toX") int toX,
                             @PathVariable("toY") int toY) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, fromX, fromY);
        return this.dungeonService.move(location, toX, toY);
    }

    @GetMapping("/{dungeonId}/what/{playerId}/{level}/{x}/{y}")
    public ThingOverviewType whatIsOnMyFeet(@PathVariable("dungeonId") int dungeonId,
                                            @PathVariable("playerId") int playerId,
                                            @PathVariable("level") int level,
                                            @PathVariable("x") int x,
                                            @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.whatIsOnMyFeet(location);
    }

    @PostMapping("/{dungeonId}/upstairs/{playerId}/{level}/{x}/{y}")
    public DungeonLocation upStairs(@PathVariable("dungeonId") int dungeonId,
                                    @PathVariable("playerId") int playerId,
                                    @PathVariable("level") int level,
                                    @PathVariable("x") int x,
                                    @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x,y);
        return this.dungeonService.upStairs(location);
    }

    @PostMapping("/{dungeonId}/downstairs/{playerId}/{level}/{x}/{y}")
    public DungeonLocation downStairs(@PathVariable("dungeonId") int dungeonId,
                                      @PathVariable("playerId") int playerId,
                                      @PathVariable("level") int level,
                                      @PathVariable("x") int x,
                                      @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.downStairs(location);
    }

    @PostMapping("/{dungeonId}/pickup/gold/{playerId}/{level}/{x}/{y}")
    public Gold pickUpGold(@PathVariable("dungeonId") int dungeonId,
                           @PathVariable("playerId") int playerId,
                           @PathVariable("level") int level,
                           @PathVariable("x") int x,
                           @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.pickUpGold(location);
    }

    @PostMapping("/{dungeonId}/pickup/object/{playerId}/{level}/{x}/{y}")
    public int pickUpObject(@PathVariable("dungeonId") int dungeonId,
                            @PathVariable("playerId") int playerId,
                            @PathVariable("level") int level,
                            @PathVariable("x") int x,
                            @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.pickUpObject(location);
    }

    @GetMapping("/{dungeonId}/name")
    public String getDungeonName(@PathVariable("dungeonId") int dungeonId) {
        return this.dungeonService.getDungeonName(dungeonId);
    }

    @GetMapping("/{dungeonId}/display/{playerId}/{level}/{x}/{y}")
    public List<List<String>> displayData(@PathVariable("dungeonId") int dungeonId,
                                          @PathVariable("playerId") int playerId,
                                          @PathVariable("level") int level,
                                          @PathVariable("x") int x,
                                          @PathVariable("y") int y) {
        DungeonLocation location = new DungeonLocation(dungeonId, playerId, level, x, y);
        return this.dungeonService.displayData(location);
    }

    @GetMapping("/name/{dungeonName}")
    public DungeonDto findByName(@PathVariable String dungeonName) {
        return this.dungeonService.findByName(dungeonName);
    }

    @PutMapping("/name/{dungeonName}")
    public int save(@PathVariable("dungeonName") String dungeonName) {
        return this.dungeonService.save(dungeonName);
    }
}
