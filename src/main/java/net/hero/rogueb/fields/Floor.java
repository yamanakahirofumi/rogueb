package net.hero.rogueb.fields;

import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.FloorDomain;
import net.hero.rogueb.dungeon.ObjectCoodinateDomain;
import net.hero.rogueb.math.Random;
import net.hero.rogueb.object.Thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Floor {

    private final String id;
    /**
     * 階層数
     */
    private final int level;
    private final int itemLimitCount;
    private final Space space;
    private final Map<Coordinate2D, PlayerDto> playerDtoMap;
    private final Map<Coordinate2D, Thing> things;

    /**
     * 新規作成時
     *
     * @param level 階層数
     */
    public Floor(int level) {
        this.id = "#new";
        this.level = level;
        this.playerDtoMap = new HashMap<>();
        this.things = new HashMap<>();
        this.space = new Space();
        this.itemLimitCount = Random.rnd(3);
    }

    /**
     * ロード時
     *
     * @param floorDomain 保存したデータ
     */
    public Floor(FloorDomain floorDomain, Map<Integer, Thing> thingMap) {
        this.id = floorDomain.getId();
        this.level = floorDomain.getLevel();
        this.playerDtoMap = new HashMap<>();
        this.things = new HashMap<>();
        for (var thing : floorDomain.getThingList()) {
            this.things.put(thing.getPosition(), thingMap.get(thing.getObjectId()));
        }
        this.space = new Space(floorDomain.getUpStairs(), floorDomain.getDownStairs());
        this.itemLimitCount = 0;
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
            fields.get(coordinate.getY()).set(coordinate.getX(), s.getValue().getDisplay());
        }
        for (var p : this.playerDtoMap.entrySet()) {
            Coordinate2D coordinate2D = p.getKey();
            fields.get(coordinate2D.getY()).set(coordinate2D.getX(), "@");
        }
        return fields;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
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

    public int getItemCreateCount() {
        int count = this.itemLimitCount - this.things.size();
        return Math.max(count, 0);
    }

    public void setThings(List<Thing> thingList) {
        for (var thing : thingList) {
            while (true) {
                Coordinate2D rndPosition = this.space.getRndPosition();
                if (!this.things.containsKey(rndPosition)) {
                    this.things.put(rndPosition, thing);
                    break;
                }
            }
        }
    }

    public List<ObjectCoodinateDomain> getSymbolThings() {
        List<ObjectCoodinateDomain> symbolThings = new ArrayList<>();
        for (var thing : this.things.entrySet()) {
            ObjectCoodinateDomain objectCoodinateDomain = new ObjectCoodinateDomain();
            objectCoodinateDomain.setPosition(thing.getKey());
            objectCoodinateDomain.setObjectId(thing.getValue().getId());
            symbolThings.add(objectCoodinateDomain);
        }
        return symbolThings;
    }
}
