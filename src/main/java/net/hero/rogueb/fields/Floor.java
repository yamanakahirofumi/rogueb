package net.hero.rogueb.fields;

import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.FloorDomain;
import net.hero.rogueb.object.Thing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Floor {

    /**
     * 階層
     */
    private final int level;
    private final Space space;
    private final Map<Coordinate2D, PlayerDto> playerDtoMap;
    private Map<Coordinate2D, Thing> things;

    public Floor(int level) {
        this.level = level;
        this.playerDtoMap = new HashMap<>();
        this.things = new HashMap<>();
        this.space = new Space();
    }

    public Floor(FloorDomain floorDomain){
        this.level = floorDomain.getLevel();
        this.playerDtoMap = new HashMap<>();
        this.things = new HashMap<>();
        this.space = new Space(floorDomain.getUpStairs(), floorDomain.getDownStairs());
    }

    public Coordinate2D enterFromUpStairs(PlayerDto playerDto) {
        Coordinate2D coordinate2D = this.space.getUpStairs();
        this.playerDtoMap.put(coordinate2D, playerDto);
        return coordinate2D;
    }

    public Coordinate2D move(PlayerDto playerDto) {
        var locationDto = playerDto.getLocationDto();
        Coordinate2D coordinate2D = new Coordinate2D(locationDto.getX(), locationDto.getY());
        this.playerDtoMap.put(coordinate2D, playerDto);
        return coordinate2D;
    }

    private List<List<String>> changeDisplay() {
        var fields = this.space.getDisplay();
        for (var s : this.things.entrySet()) {
            Coordinate2D coordinate = s.getKey();
            fields.get(coordinate.getX()).set(coordinate.getY(), s.getValue().getDisplay());
        }
        for (var p : this.playerDtoMap.entrySet()) {
            Coordinate2D coordinate2D = p.getKey();
            fields.get(coordinate2D.getX()).set(coordinate2D.getY(), "@");
        }
        return fields;
    }

    public List<List<String>> getFields() {
        return this.changeDisplay();
    }

    public boolean isObject(Coordinate2D position) {
        return this.things.containsKey(position);
    }

    public Thing getThings(Coordinate2D position) {
        return this.things.get(position);
    }

    public void removeThings(Coordinate2D position) {
        this.things.remove(position);
    }

    public Coordinate2D getUpStairs() {
        return this.space.getUpStairs();
    }

    public Coordinate2D getDownStairs() {
        return this.space.getDownStairs();
    }
}
