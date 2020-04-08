package net.hero.rogueb.services;

import net.hero.rogueb.bookofadventure.BookOfAdventureService;
import net.hero.rogueb.bookofadventure.dto.LocationDto;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.character.Human;
import net.hero.rogueb.character.Player;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.dungeon.DungeonService;
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

    public PlayerService(WorldService worldService,BookOfAdventureService bookOfAdventureService, DungeonService dungeonService) {
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
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userName);
        Coordinate2D move = this.dungeonService.move(playerDto, MoveEnum.Top);
        playerDto.getLocationDto().setX(move.getX());
        playerDto.getLocationDto().setY(move.getY());
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }

    public Map<String, Boolean> down(String userName) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userName);
        Coordinate2D move = this.dungeonService.move(playerDto, MoveEnum.Down);
        playerDto.getLocationDto().setX(move.getX());
        playerDto.getLocationDto().setY(move.getY());
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }

    public Map<String, Boolean> right(String userName) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userName);
        Coordinate2D move = this.dungeonService.move(playerDto, MoveEnum.Right);
        playerDto.getLocationDto().setX(move.getX());
        playerDto.getLocationDto().setY(move.getY());
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }

    public Map<String, Boolean> left(String userName) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userName);
        Coordinate2D move = this.dungeonService.move(playerDto, MoveEnum.Left);
        playerDto.getLocationDto().setX(move.getX());
        playerDto.getLocationDto().setY(move.getY());
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }

    public Map<String, Boolean> pickup(String userName) {
        return Map.of("result", Human.getInstance(userName).pickUp());
    }
}
