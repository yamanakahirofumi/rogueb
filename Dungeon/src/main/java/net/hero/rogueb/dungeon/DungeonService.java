package net.hero.rogueb.dungeon;

import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.dungeon.dto.DungeonPlayerDto;
import net.hero.rogueb.dungeon.fields.Coordinate2D;
import net.hero.rogueb.dungeon.fields.DungeonLocation;
import net.hero.rogueb.dungeon.fields.Floor;
import net.hero.rogueb.dungeon.fields.Gold;
import net.hero.rogueb.dungeon.fields.ThingOverviewType;
import net.hero.rogueb.dungeon.mapper.DungeonMapper;
import net.hero.rogueb.dungeon.repositories.FloorRepository;
import net.hero.rogueb.objectclient.ObjectServiceClient;
import net.hero.rogueb.objectclient.o.ThingSimple;
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
    private final ObjectServiceClient objectServiceClient;

    public DungeonService(DungeonMapper dungeonMapper, FloorRepository floorRepository, ObjectServiceClient objectServiceClient) {
        this.dungeonMapper = dungeonMapper;
        this.floorRepository = floorRepository;
        this.objectServiceClient = objectServiceClient;
    }

    public DungeonDto findByName(String name) {
        return this.dungeonMapper.findByName(name);
    }

    public int save(String name) {
        DungeonDto dungeonDto = new DungeonDto(name, 1, "localhost", 3);
        return this.dungeonMapper.insert(dungeonDto);
    }

    public List<List<String>> displayData(DungeonLocation location) {
        Floor floor = this.getFloor(location);
        floor.move(location.getCoordinate2D(), location.getPlayerId());
        return floor.getFields();
    }

    public String getDungeonName(int dungeonId) {
        DungeonDto dungeonDto = this.dungeonMapper.findById(dungeonId);
        return dungeonDto.getName();
    }

    public DungeonLocation gotoDungeon(int dungeonId, int playerId) {
        DungeonDto dungeonDto = this.dungeonMapper.findById(dungeonId);
        DungeonLocation dungeonLocation = this.down(dungeonDto, 1, playerId);
        DungeonPlayerDto dungeonPlayerDto = new DungeonPlayerDto();
        dungeonPlayerDto.setDungeonId(dungeonDto.getId());
        dungeonPlayerDto.setPlayerId(playerId);
        dungeonPlayerDto.setLevel(1);
        this.dungeonMapper.deleteDungeonPlayer(dungeonPlayerDto);
        this.dungeonMapper.insertDungeonPlayer(dungeonPlayerDto);
        return dungeonLocation;
    }

    private DungeonLocation down(DungeonDto dungeonDto, int level, int playerId) {
        List<FloorDomain> floorDomainList = this.floorRepository.findByDungeonIdAndLevelAndUserId(dungeonDto.getId(), level, playerId);
        Coordinate2D coordinate2D;
        if (floorDomainList.size() > 0) {
            FloorDomain floorDomain = floorDomainList.get(0);
            coordinate2D = floorDomain.getUpStairs();
        } else {
            Floor floor = new Floor(level, dungeonDto);
            floor.setObjects(this.objectServiceClient.createObjects(floor.getItemCreateCount()));
            coordinate2D = floor.enterFromUpStairs(playerId);
            this.saveFloor(floor, playerId);
        }
        return new DungeonLocation(dungeonDto.getId(), playerId, level, coordinate2D);
    }

    private DungeonLocation up(int dungeonId, int level, int playerId) {
        List<FloorDomain> floorDomainList = this.floorRepository.findByDungeonIdAndLevelAndUserId(dungeonId, level, playerId);
        if (floorDomainList.size() < 1) {
            throw new RuntimeException("Data不整合");
        }
        FloorDomain floorDomain = floorDomainList.get(0);
        return new DungeonLocation(dungeonId, playerId, level, floorDomain.getDownStairs());
    }

    public Coordinate2D move(DungeonLocation location, int toX, int toY) {
        Floor floor = this.getFloor(location);
        Coordinate2D coordinate2D = new Coordinate2D(toX, toY);
        boolean result = !floor.getFields().get(coordinate2D.getY()).get(coordinate2D.getX()).equals("#");
        if (result) {
            return coordinate2D;
        }
        return location.getCoordinate2D();
    }

    public DungeonLocation downStairs(DungeonLocation location) {
        Floor floor = this.getFloor(location);
        if (!floor.getDownStairs().equals(location.getCoordinate2D())) {
            return location;
        }
        DungeonDto dungeonDto = this.dungeonMapper.findById(location.getDungeonId());
        if (dungeonDto.getMaxLevel() <= location.getLevel()) {
            return location;
        }
        DungeonLocation newLocation = this.down(dungeonDto, location.getLevel() + 1, location.getPlayerId());
        DungeonPlayerDto dungeonPlayerDto = new DungeonPlayerDto();
        dungeonPlayerDto.setDungeonId(location.getDungeonId());
        dungeonPlayerDto.setPlayerId(location.getPlayerId());
        dungeonPlayerDto.setLevel(location.getLevel() + 1);
        this.dungeonMapper.deleteDungeonPlayer(dungeonPlayerDto);
        this.dungeonMapper.insertDungeonPlayer(dungeonPlayerDto);
        return newLocation;
    }

    public DungeonLocation upStairs(DungeonLocation location) {
        Floor floor = this.getFloor(location);
        if (!floor.getUpStairs().equals(location.getCoordinate2D())) {
            return location;
        }
        if (location.getLevel() == 1) {
            // TODO:暫定対応
            return location;
        }
        return this.up(location.getDungeonId(), location.getLevel() - 1, location.getPlayerId());
    }

    public int pickUpObject(DungeonLocation location) {
        Floor floor = this.getFloor(location);
        if (!floor.isObject(location.getCoordinate2D())) {
            return 0;
        }
        ThingSimple things = floor.getObject(location.getCoordinate2D());
        floor.removeObject(location.getCoordinate2D());
        this.saveFloor(floor, location.getPlayerId());

        return things.id();
    }

    public Gold pickUpGold(DungeonLocation location) {
        Floor floor = this.getFloor(location);
        if (!floor.isGold(location.getCoordinate2D())) {
            return null;
        }
        Gold gold = floor.getGold(location.getCoordinate2D());
        floor.removeGold(location.getCoordinate2D());
        this.saveFloor(floor, location.getPlayerId());

        return gold;
    }


    public ThingOverviewType whatIsOnMyFeet(DungeonLocation dungeonLocation) {
        Floor floor = this.getFloor(dungeonLocation);
        if (floor.isGold(dungeonLocation.getCoordinate2D())) {
            return ThingOverviewType.Gold;
        }
        if (floor.isObject(dungeonLocation.getCoordinate2D())) {
            return ThingOverviewType.Object;
        }
        return ThingOverviewType.None;
    }

    private Floor getFloor(DungeonLocation location) {
        List<FloorDomain> floorDomainList = this.floorRepository
                .findByDungeonIdAndLevelAndUserId(location.getDungeonId(), location.getLevel(), location.getPlayerId());
        FloorDomain floorDomain = floorDomainList.get(0);
        Map<Integer, ThingSimple> objects = this.objectServiceClient.getObjects(floorDomain.getThingList().stream()
                .map(ObjectCoordinateDomain::getObjectId)
                .collect(Collectors.toList())).block();
        return new Floor(floorDomain, objects);
    }

    private void saveFloor(Floor floor, int playerId) {
        FloorDomain floorDomain = new FloorDomain();
        floorDomain.setId(floor.getId());
        floorDomain.setDungeonId(floor.getDungeonId());
        floorDomain.setLevel(floor.getLevel());
        floorDomain.setUserId(playerId);
        floorDomain.setDownStairs(floor.getDownStairs());
        floorDomain.setUpStairs(floor.getUpStairs());
        floorDomain.setThingList(floor.getSymbolObjects());
        floorDomain.setGoldList(floor.getSymbolGolds());

        this.floorRepository.save(floorDomain);

    }
}
