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
import net.hero.rogueb.object.ObjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        DungeonDto dungeonDto = new DungeonDto(name, 1, "localhost");
        return this.dungeonMapper.insert(dungeonDto);
    }

    public List<List<String>> displayData(PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocationDto();
        List<FloorDomain> floorDomainList = this.floorRepository.findByDungeonIdAndLevelAndUserId(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        Floor floor = new Floor(floorDomainList.get(0));
        floor.move(playerDto);
        return floor.getFields();
    }

    public LocationDto gotoDungeon(DungeonDto dungeonDto, PlayerDto playerDto) {
        LocationDto locationDto = this.down(dungeonDto, 1, playerDto);
        locationDto.setDungeonId(dungeonDto.getId());
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
        Floor floor = new Floor(level);
        this.objectService.createRing(); // すこし違う
        Coordinate2D coordinate2D = floor.enterFromUpStairs(playerDto);
        FloorDomain floorDomain = new FloorDomain();
        floorDomain.setDungeonId(dungeonDto.getId());
        floorDomain.setLevel(1);
        floorDomain.setUserId(playerDto.getId());
        floorDomain.setDownStairs(floor.getDownStairs());
        floorDomain.setUpStairs(floor.getUpStairs());
        this.floorRepository.save(floorDomain);
        locationDto.setLevel(level);
        locationDto.setX(coordinate2D.getX());
        locationDto.setY(coordinate2D.getY());
        return locationDto;
    }


    private void up() {

    }

    public Coordinate2D move(PlayerDto playerDto, MoveEnum moveEnum) {
        LocationDto locationDto = playerDto.getLocationDto();
        List<FloorDomain> floorDomainList = this.floorRepository.findByDungeonIdAndLevelAndUserId(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        Floor floor = new Floor(floorDomainList.get(0));
        Coordinate2D coordinate2D = new Coordinate2D(locationDto.getX() + moveEnum.getX(), locationDto.getY() + moveEnum.getY());
        boolean result = !floor.getFields().get(coordinate2D.getX()).get(coordinate2D.getY()).equals("#");
        if (result) {
            return coordinate2D;
        }
        return new Coordinate2D(locationDto.getX(), locationDto.getY());
    }

    public void pickUp(PlayerDto playerDto){
        LocationDto locationDto = playerDto.getLocationDto();
        Coordinate2D position = new Coordinate2D(locationDto.getX(), locationDto.getY());
        List<FloorDomain> floorDomainList = this.floorRepository.findByDungeonIdAndLevelAndUserId(locationDto.getDungeonId(), locationDto.getLevel(), playerDto.getId());
        Floor floor = new Floor(floorDomainList.get(0));
        if (!floor.isObject(position)) {
            // return false;
        }
//        boolean result = this.bag.addContents(this.location.floor.getThings(position));
//        if (result) {
//            floor.removeThings(position);
//        }
//        return result;
    }
}
