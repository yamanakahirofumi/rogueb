package net.hero.rogueb.services;

import net.hero.rogueb.bag.Bag;
import net.hero.rogueb.bookofadventure.BookOfAdventureService;
import net.hero.rogueb.bookofadventure.dto.LocationDto;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.DungeonService;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.fields.Coordinate2D;
import net.hero.rogueb.fields.MoveEnum;
import net.hero.rogueb.objects.Gold;
import net.hero.rogueb.objects.ObjectService;
import net.hero.rogueb.objects.Thing;
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

    public Map<String, Object> pickup(int userId) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userId);
        return switch (this.dungeonService.whatIsOnMyFeet(playerDto)) {
            case None -> Map.of("result", false, "message", "NoObjectOnTheFloor");
            case Gold -> this.pickupGold(playerDto);
            case Object -> this.pickupObject(playerDto);
        };
    }

    private Map<String, Object> pickupGold(PlayerDto playerDto) {
        Gold gold = this.dungeonService.pickUpGold(playerDto);
        if (gold == null) {
            return Map.of("result", false, "message", "NoObjectOnTheFloor");
        }
        playerDto.setGold(playerDto.getGold() + gold.getGold());
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true, "type", 1, "gold", gold.getGold());
    }


    private Map<String, Object> pickupObject(PlayerDto playerDto) {
        Bag bag = new Bag();
        List<Integer> itemList = this.bookOfAdventureService.getItemList(playerDto.getId());
        Map<Integer, Thing> thingList = this.objectService.getObjects(itemList);
        bag.setContents(new ArrayList<>(thingList.values()));
        if (bag.getEmptySize() <= 0) {
            return Map.of("result", false, "message", "BagNoEmpty");
        }
        Thing thing = this.dungeonService.pickUpObject(playerDto);
        if (thing == null) {
            return Map.of("result", false, "message", "NoObjectOnTheFloor");
        }
        List<Integer> itemIdList = bag.getThingIdList();
        itemIdList.add(thing.getId());
        this.bookOfAdventureService.changeObject(playerDto.getId(), itemIdList);
        return Map.of("result", true, "type", 2, "itemName", thing.getName());
    }

    public Map<String, Boolean> downStairs(int userId) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userId);
        LocationDto locationDto = this.dungeonService.downStairs(playerDto);
        playerDto.setLocationDto(locationDto);
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }

    public Map<String, Boolean> upStairs(int userId) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userId);
        LocationDto locationDto = this.dungeonService.upStairs(playerDto);
        playerDto.setLocationDto(locationDto);
        this.bookOfAdventureService.save(playerDto);
        return Map.of("result", true);
    }
}
