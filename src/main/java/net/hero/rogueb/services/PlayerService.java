package net.hero.rogueb.services;

import net.hero.rogueb.bookofadventure.BookOfAdventureService;
import net.hero.rogueb.bookofadventure.dto.LocationDto;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.character.Human;
import net.hero.rogueb.dungeon.DungeonService;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.fields.Coordinate2D;
import net.hero.rogueb.fields.MoveEnum;
import net.hero.rogueb.world.WorldService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlayerService {
    private final WorldService worldService;
    private final BookOfAdventureService bookOfAdventureService;
    private final DungeonService dungeonService;

    public PlayerService(WorldService worldService, BookOfAdventureService bookOfAdventureService, DungeonService dungeonService) {
        this.worldService = worldService;
        this.bookOfAdventureService = bookOfAdventureService;
        this.dungeonService = dungeonService;
    }

    public Map<String, String> create(String userName) {
        PlayerDto playerDto = this.worldService.bornPlayer(userName);
        DungeonDto dungeonDto = this.worldService.getStartDungeon();
        LocationDto locationDto = this.dungeonService.gotoDungeon(dungeonDto, playerDto);
        playerDto.setLocationDto(locationDto);
        this.bookOfAdventureService.save(playerDto);
        return Map.of("key", "Hello World");
    }

    public Map<String, Boolean> top(String userName) {
        return this.move(userName, MoveEnum.Top);
    }

    public Map<String, Boolean> down(String userName) {
        return this.move(userName, MoveEnum.Down);
    }

    public Map<String, Boolean> right(String userName) {
        return this.move(userName, MoveEnum.Right);
    }

    public Map<String, Boolean> left(String userName) {
        return this.move(userName, MoveEnum.Left);
    }

    public Map<String, Boolean> move(String userName, MoveEnum moveEnum) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userName);
        Coordinate2D move = this.dungeonService.move(playerDto, moveEnum);
        playerDto.getLocationDto().setX(move.getX());
        playerDto.getLocationDto().setY(move.getY());
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }

    public Map<String, Boolean> pickup(String userName) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userName);
        this.dungeonService.pickUp(playerDto);
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }
}
