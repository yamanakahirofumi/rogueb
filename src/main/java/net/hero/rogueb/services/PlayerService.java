package net.hero.rogueb.services;

import net.hero.rogueb.bag.Bag;
import net.hero.rogueb.bookofadventure.BookOfAdventureService;
import net.hero.rogueb.bookofadventure.dto.LocationDto;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.DungeonService;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.fields.Coordinate2D;
import net.hero.rogueb.fields.MoveEnum;
import net.hero.rogueb.object.ObjectService;
import net.hero.rogueb.object.Thing;
import net.hero.rogueb.world.WorldService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PlayerService {
    private final WorldService worldService;
    private final BookOfAdventureService bookOfAdventureService;
    private final DungeonService dungeonService;
    private final ObjectService objectService;

    public PlayerService(WorldService worldService, BookOfAdventureService bookOfAdventureService,
                         DungeonService dungeonService, ObjectService objectService) {
        this.worldService = worldService;
        this.bookOfAdventureService = bookOfAdventureService;
        this.dungeonService = dungeonService;
        this.objectService = objectService;
    }

    public Map<String, String> gotoDungeon(int userId) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userId);
        DungeonDto dungeonDto = this.worldService.getStartDungeon();
        LocationDto locationDto = this.dungeonService.gotoDungeon(dungeonDto, playerDto);
        playerDto.setLocationDto(locationDto);
        this.bookOfAdventureService.save(playerDto);
        return Map.of("key", "Hello World");
    }

    public Map<String, Boolean> top(int userId) {
        return this.move(userId, MoveEnum.Top);
    }

    public Map<String, Boolean> down(int userId) {
        return this.move(userId, MoveEnum.Down);
    }

    public Map<String, Boolean> right(int userId) {
        return this.move(userId, MoveEnum.Right);
    }

    public Map<String, Boolean> left(int userId) {
        return this.move(userId, MoveEnum.Left);
    }

    public Map<String, Boolean> move(int userId, MoveEnum moveEnum) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userId);
        Coordinate2D move = this.dungeonService.move(playerDto, moveEnum);
        playerDto.getLocationDto().setX(move.getX());
        playerDto.getLocationDto().setY(move.getY());
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }

    public Map<String, Boolean> pickup(int userId) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userId);
        Bag bag = new Bag();
        List<Integer> itemList = this.bookOfAdventureService.getItemList(userId);
        Map<Integer, Thing> thingList = this.objectService.getObjects(itemList);
        bag.setContents(new ArrayList<>(thingList.values()));
        if (bag.getEmptySize() <= 0) {
            return Map.of("result", false);
        }
        Thing thing = this.dungeonService.pickUp(playerDto);
        if (thing == null) {
            return Map.of("result", false);
        }
        List<Integer> itemIdList = bag.getThingIdList();
        itemIdList.add(thing.getId());
        this.bookOfAdventureService.changeObject(userId, itemIdList);
        return Map.of("result", true);
    }
}
