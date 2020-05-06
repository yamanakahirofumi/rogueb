package net.hero.rogueb.dungeon;

import net.hero.rogueb.bookofadventure.dto.LocationDto;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.dungeon.dto.DungeonPlayerDto;
import net.hero.rogueb.dungeon.mapper.DungeonMapper;
import net.hero.rogueb.dungeon.repositories.FloorRepository;
import net.hero.rogueb.fields.Coordinate2D;
import net.hero.rogueb.fields.Floor;
import net.hero.rogueb.fields.MoveEnum;
import net.hero.rogueb.objects.Gold;
import net.hero.rogueb.objects.ThingOverviewType;
import net.hero.rogueb.objects.ObjectService;
import net.hero.rogueb.objects.Thing;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class DungeonService {
    private final DungeonMapper dungeonMapper;
    private final FloorRepository floorRepository;
    private final ObjectService objectService;

    public DungeonService(DungeonMapper dungeonMapper, FloorRepository floorRepository, ObjectService objectService) {
        this.dungeonMapper = dungeonMapper;
        this.floorRepository = floorRepository;
        this.objectService = objectService;
    }

    public DungeonDto findByName(String name) {
        return this.dungeonMapper.findByName(name);
    }

    public int save(String name) {
        DungeonDto dungeonDto = new DungeonDto(name, 1, "localhost", 3);
        return this.dungeonMapper.insert(dungeonDto);
    }

    public List<List<String>> displayData(PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocationDto();
        Floor floor = this.getFloor(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        floor.move(playerDto);
        return floor.getFields();
    }

    public String getDungeonName(PlayerDto playerDto) {
        DungeonDto dungeonDto = this.dungeonMapper.findById(playerDto.getLocationDto().getDungeonId());
        return dungeonDto.getName();
    }

    public LocationDto gotoDungeon(DungeonDto dungeonDto, PlayerDto playerDto) {
        LocationDto locationDto = this.down(dungeonDto, 1, playerDto);
        DungeonPlayerDto dungeonPlayerDto = new DungeonPlayerDto();
        dungeonPlayerDto.setDungeonId(dungeonDto.getId());
        dungeonPlayerDto.setPlayerId(playerDto.getId());
        dungeonPlayerDto.setLevel(1);
        this.dungeonMapper.deleteDungeonPlayer(dungeonPlayerDto);
        this.dungeonMapper.insertDungeonPlayer(dungeonPlayerDto);
        return locationDto;
    }

    private LocationDto down(DungeonDto dungeonDto, int level, PlayerDto playerDto) {
        var locationDto = new LocationDto();
        locationDto.setDungeonId(dungeonDto.getId());
        locationDto.setLevel(level);
        Coordinate2D coordinate2D;

        List<FloorDomain> floorDomainList = this.floorRepository.findByDungeonIdAndLevelAndUserId(dungeonDto.getId(), level, playerDto.getId());
        if (floorDomainList.size() > 0) {
            FloorDomain floorDomain = floorDomainList.get(0);
            coordinate2D = floorDomain.getUpStairs();
        } else {
            Floor floor = new Floor(level, dungeonDto);
            floor.setObjects(this.objectService.createObjects(floor.getItemCreateCount()));
            coordinate2D = floor.enterFromUpStairs(playerDto);
            this.saveFloor(floor, playerDto);
        }
        locationDto.setX(coordinate2D.getX());
        locationDto.setY(coordinate2D.getY());
        return locationDto;
    }

    private LocationDto up(int dungeonId, int level, PlayerDto playerDto) {
        var locationDto = new LocationDto();
        locationDto.setDungeonId(dungeonId);
        locationDto.setLevel(level);

        List<FloorDomain> floorDomainList = this.floorRepository.findByDungeonIdAndLevelAndUserId(dungeonId, level, playerDto.getId());
        if (floorDomainList.size() < 1) {
            throw new RuntimeException("Data不整合");
        }
        FloorDomain floorDomain = floorDomainList.get(0);
        Coordinate2D coordinate2D = floorDomain.getDownStairs();
        locationDto.setX(coordinate2D.getX());
        locationDto.setY(coordinate2D.getY());
        return locationDto;
    }

    public Coordinate2D move(PlayerDto playerDto, MoveEnum moveEnum) {
        LocationDto locationDto = playerDto.getLocationDto();
        Floor floor = this.getFloor(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        Coordinate2D coordinate2D = new Coordinate2D(locationDto.getX() + moveEnum.getX(), locationDto.getY() + moveEnum.getY());
        boolean result = !floor.getFields().get(coordinate2D.getY()).get(coordinate2D.getX()).equals("#");
        if (result) {
            return coordinate2D;
        }
        return new Coordinate2D(locationDto.getX(), locationDto.getY());
    }

    public LocationDto downStairs(PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocationDto();
        Coordinate2D position = new Coordinate2D(locationDto.getX(), locationDto.getY());
        Floor floor = this.getFloor(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        if (!floor.getDownStairs().equals(position)) {
            return locationDto;
        }
        DungeonDto dungeonDto = this.dungeonMapper.findById(locationDto.getDungeonId());
        if (dungeonDto.getMaxLevel() <= locationDto.getLevel()) {
            return locationDto;
        }
        LocationDto newLocation = this.down(dungeonDto, locationDto.getLevel() + 1, playerDto);
        DungeonPlayerDto dungeonPlayerDto = new DungeonPlayerDto();
        dungeonPlayerDto.setDungeonId(locationDto.getDungeonId());
        dungeonPlayerDto.setPlayerId(playerDto.getId());
        dungeonPlayerDto.setLevel(locationDto.getLevel() + 1);
        this.dungeonMapper.deleteDungeonPlayer(dungeonPlayerDto);
        this.dungeonMapper.insertDungeonPlayer(dungeonPlayerDto);
        return newLocation;

    }

    public LocationDto upStairs(PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocationDto();
        Coordinate2D position = new Coordinate2D(locationDto.getX(), locationDto.getY());
        Floor floor = this.getFloor(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        if (!floor.getUpStairs().equals(position)) {
            return locationDto;
        }
        if (locationDto.getLevel() == 1) {
            // TODO:暫定対応
            return locationDto;
        }
        return this.up(locationDto.getDungeonId(), locationDto.getLevel() - 1, playerDto);
    }

    public Thing pickUpObject(PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocationDto();
        Coordinate2D position = new Coordinate2D(locationDto.getX(), locationDto.getY());
        Floor floor = this.getFloor(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        if (!floor.isObject(position)) {
            return null;
        }
        Thing things = floor.getObject(position);
        floor.removeObject(position);
        this.saveFloor(floor, playerDto);

        return things;
    }

    public Gold pickUpGold(PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocationDto();
        Coordinate2D position = new Coordinate2D(locationDto.getX(), locationDto.getY());
        Floor floor = this.getFloor(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        if (!floor.isGold(position)) {
            return null;
        }
        Gold gold = floor.getGold(position);
        floor.removeGold(position);
        this.saveFloor(floor, playerDto);

        return gold;
    }


    public ThingOverviewType whatIsOnMyFeet(PlayerDto playerDto){
        LocationDto locationDto = playerDto.getLocationDto();
        Coordinate2D position = new Coordinate2D(locationDto.getX(), locationDto.getY());
        Floor floor = this.getFloor(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        if(floor.isGold(position)){
            return ThingOverviewType.Gold;
        }
        if(floor.isObject(position)){
            return ThingOverviewType.Object;
        }
        return ThingOverviewType.None;
    }

    private Floor getFloor(int dungeonId, int level, int playerId) {
        List<FloorDomain> floorDomainList = this.floorRepository.findByDungeonIdAndLevelAndUserId(dungeonId, level, playerId);
        FloorDomain floorDomain = floorDomainList.get(0);
        Map<Integer, Thing> objects = this.objectService.getObjects(floorDomain.getThingList().stream()
                .map(ObjectCoordinateDomain::getObjectId)
                .collect(Collectors.toList()));
        return new Floor(floorDomain, objects);
    }

    private void saveFloor(Floor floor,PlayerDto playerDto){
        FloorDomain floorDomain = new FloorDomain();
        floorDomain.setId(floor.getId());
        floorDomain.setDungeonId(floor.getDungeonId());
        floorDomain.setLevel(floor.getLevel());
        floorDomain.setUserId(playerDto.getId());
        floorDomain.setDownStairs(floor.getDownStairs());
        floorDomain.setUpStairs(floor.getUpStairs());
        floorDomain.setThingList(floor.getSymbolObjects());
        floorDomain.setGoldList(floor.getSymbolGolds());

        this.floorRepository.save(floorDomain);

    }
}
