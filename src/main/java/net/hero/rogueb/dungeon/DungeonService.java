package net.hero.rogueb.dungeon;

import net.hero.rogueb.bookofadventure.dto.LocationDto;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.dungeon.dto.DungeonPlayerDto;
import net.hero.rogueb.dungeon.mapper.DungeonMapper;
import net.hero.rogueb.fields.Coordinate2D;
import net.hero.rogueb.fields.Dungeon;
import net.hero.rogueb.fields.MoveEnum;
import net.hero.rogueb.object.ObjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class DungeonService {
    private final DungeonMapper dungeonMapper;
    private ObjectService objectService;

    public DungeonService(DungeonMapper dungeonMapper, ObjectService objectService) {
        this.dungeonMapper = dungeonMapper;
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
        DungeonDto dungeonDto = this.dungeonMapper.findByIdAndPlayerId(playerDto.getLocationDto().getDungeonId(), playerDto.getId());
        Dungeon dungeon = new Dungeon(dungeonDto);
        dungeon.move(playerDto);
        return dungeon.getFields();
    }

    public LocationDto gotoDungeon(DungeonDto dungeonDto, PlayerDto playerDto) {
        Dungeon dungeon = new Dungeon(dungeonDto);
        LocationDto locationDto = this.down(dungeon, 1, playerDto);
        locationDto.setDungeonId(dungeonDto.getId());
        DungeonPlayerDto dungeonPlayerDto = new DungeonPlayerDto();
        dungeonPlayerDto.setDungeonId(dungeonDto.getId());
        dungeonPlayerDto.setPlayerId(playerDto.getId());
        dungeonPlayerDto.setLevel(1);
        this.dungeonMapper.deleteDungeonPlayer(dungeonPlayerDto);
        this.dungeonMapper.insertDungeonPlayer(dungeonPlayerDto);
        return locationDto;
    }

    private LocationDto down(Dungeon dungeon, int level, PlayerDto playerDto) {
        var locationDto = new LocationDto();
        dungeon.createFloor(level);
        this.objectService.createRing(); // すこし違う
        Coordinate2D coordinate2D = dungeon.down(level, playerDto);
        locationDto.setLevel(level);
        locationDto.setX(coordinate2D.getX());
        locationDto.setY(coordinate2D.getY());
        return locationDto;
    }


    private void up() {

    }

    public Coordinate2D move(PlayerDto playerDto, MoveEnum moveEnum) {
        DungeonDto dungeonDto = this.dungeonMapper.findByIdAndPlayerId(playerDto.getLocationDto().getDungeonId(), playerDto.getId());
        Dungeon dungeon = new Dungeon(dungeonDto);
        Coordinate2D coordinate2D = new Coordinate2D(playerDto.getLocationDto().getX() + moveEnum.getX(), playerDto.getLocationDto().getY()+ moveEnum.getY());
        boolean result = !dungeon.getFields().get(coordinate2D.getX()).get(coordinate2D.getY()).equals("#");
        if(result){
            return coordinate2D;
        }
        return new Coordinate2D(playerDto.getLocationDto().getX(), playerDto.getLocationDto().getY());
    }
}
