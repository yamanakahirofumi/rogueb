package net.hero.rogueb.fields;

import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.FloorDomain;
import net.hero.rogueb.dungeon.GoldCoordinateDomain;
import net.hero.rogueb.dungeon.ObjectCoordinateDomain;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.math.Random;
import net.hero.rogueb.objects.Gold;
import net.hero.rogueb.objects.Thing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Floor {

    private final String id;
    private final int dungeonId;
    /**
     * 階層数
     */
    private final int level;
    private final int itemLimitCount;
    private final Space space;
    private final Map<Coordinate2D, PlayerDto> playerDtoMap;
    private final Map<Coordinate2D, Thing> objects;
    private final Map<Coordinate2D, Gold> golds;

    /**
     * 新規作成時
     *
     * @param level 階層数
     */
    public Floor(int level, DungeonDto dungeonDto) {
        this.id = null;
        this.dungeonId = dungeonDto.getId();
        this.level = level;
        this.playerDtoMap = new HashMap<>();
        this.objects = new HashMap<>();
        this.golds = new HashMap<>();
        this.space = new Space(level != dungeonDto.getMaxLevel());
        this.golds.put(this.space.getRndPosition(), new Gold(Random.rnd(level * 10 + 5)));
        this.itemLimitCount = Random.rnd(dungeonDto.getItemSeed());
    }

    /**
     * ロード時
     *
     * @param floorDomain 保存したデータ
     */
    public Floor(FloorDomain floorDomain, Map<Integer, Thing> thingMap) {
        this.id = floorDomain.getId();
        this.dungeonId = floorDomain.getDungeonId();
        this.level = floorDomain.getLevel();
        this.playerDtoMap = new HashMap<>();
        this.objects = new HashMap<>();
        this.golds = new HashMap<>();
        for (var thing : floorDomain.getThingList()) {
            this.objects.put(thing.getPosition(), thingMap.get(thing.getObjectId()));
        }
        for (var gold : floorDomain.getGoldList()) {
            this.golds.put(gold.getPosition(), new Gold(gold.getGold()));
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
        for (var s : this.objects.entrySet()) {
            Coordinate2D coordinate = s.getKey();
            fields.get(coordinate.getY()).set(coordinate.getX(), s.getValue().getDisplay());
        }
        for (var g : this.golds.entrySet()) {
            Coordinate2D coordinate = g.getKey();
            fields.get(coordinate.getY()).set(coordinate.getX(), g.getValue().getDisplay());
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

    public int getDungeonId() {
        return dungeonId;
    }

    public List<List<String>> getFields() {
        return this.changeDisplay();
    }

    public boolean isObject(Coordinate2D position) {
        return this.objects.containsKey(position);
    }

    public boolean isGold(Coordinate2D position) {
        return this.golds.containsKey(position);
    }

    public Thing getObject(Coordinate2D position) {
        return this.objects.get(position);
    }

    public Gold getGold(Coordinate2D position) {
        return this.golds.get(position);
    }

    public void removeObject(Coordinate2D position) {
        this.objects.remove(position);
    }

    public void removeGold(Coordinate2D position) {
        this.golds.remove(position);
    }

    public Coordinate2D getUpStairs() {
        return this.space.getUpStairs();
    }

    public Coordinate2D getDownStairs() {
        return this.space.getDownStairs();
    }

    public int getItemCreateCount() {
        int count = this.itemLimitCount - this.objects.size();
        return Math.max(count, 0);
    }

    public void setObjects(List<Thing> thingList) {
        for (var thing : thingList) {
            while (true) {
                Coordinate2D rndPosition = this.space.getRndPosition();
                if (!this.isObject(rndPosition) && !this.isGold(rndPosition)) {
                    this.objects.put(rndPosition, thing);
                    break;
                }
            }
        }
    }

    public List<ObjectCoordinateDomain> getSymbolObjects() {
        return this.objects.entrySet().stream()
                .map(it -> new ObjectCoordinateDomain(it.getKey(), it.getValue().getId()))
                .collect(Collectors.toList());
    }

    public List<GoldCoordinateDomain> getSymbolGolds() {
        return this.golds.entrySet().stream()
                .map(it -> new GoldCoordinateDomain(it.getKey(), it.getValue().getGold()))
                .collect(Collectors.toList());
    }
}
