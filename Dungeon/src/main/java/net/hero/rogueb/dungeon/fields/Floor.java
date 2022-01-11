package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.dungeon.domain.DungeonDomain;
import net.hero.rogueb.dungeon.domain.FloorDomain;
import net.hero.rogueb.dungeon.domain.GoldCoordinateDomain;
import net.hero.rogueb.dungeon.domain.ObjectCoordinateDomain;
import net.hero.rogueb.math.Random;
import net.hero.rogueb.objectclient.o.ThingSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Floor {

    private final String id;
    private final String dungeonId;
    /**
     * 階層数
     */
    private final int level;
    private final int itemLimitCount;
    private final Space space;
    private final Map<Coordinate2D, String> playerMap;
    private final Map<Coordinate2D, ThingSimple> objects;
    private final Map<Coordinate2D, Gold> golds;

    /**
     * 新規作成時
     *
     * @param level 階層数
     * @param dungeonDomain DungeonDomain
     */
    public Floor(int level, DungeonDomain dungeonDomain) {
        this.id = null;
        this.dungeonId = dungeonDomain.getId();
        this.level = level;
        this.playerMap = new HashMap<>();
        this.objects = new HashMap<>();
        this.golds = new HashMap<>();
        this.space = new Space(level != dungeonDomain.getMaxLevel());
        this.golds.put(this.space.getRndPosition(), new Gold(Random.rnd(level * 10 + 5)));
        this.itemLimitCount = Random.rnd(dungeonDomain.getItemSeed());
    }

    /**
     * ロード時
     *
     * @param floorDomain 保存したデータ
     * @param thingMap ObjectIdがkeyとなっているThingSimpleのMap
     */
    public Floor(FloorDomain floorDomain, Map<String, ThingSimple> thingMap) {
        this.id = floorDomain.getId();
        this.dungeonId = floorDomain.getDungeonId();
        this.level = floorDomain.getLevel();
        this.playerMap = new HashMap<>();
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

    public Coordinate2D enterFromUpStairs(String playerId) {
        Coordinate2D coordinate2D = this.space.getUpStairs();
        this.playerMap.put(coordinate2D, playerId);
        return coordinate2D;
    }

    public Coordinate2D move(Coordinate2D coordinate2D, String playerId) {
        this.playerMap.put(coordinate2D, playerId);
        return coordinate2D;
    }

    private List<DisplayData> changeDisplay() {
        var fields = this.space.getDisplay();
        for (var s : this.objects.entrySet()) {
            Coordinate2D coordinate = s.getKey();
            fields.get(coordinate.y()).set(coordinate.x(), s.getValue().display());
        }
        for (var g : this.golds.entrySet()) {
            Coordinate2D coordinate = g.getKey();
            fields.get(coordinate.y()).set(coordinate.x(), g.getValue().getDisplay());
        }
        for (var p : this.playerMap.entrySet()) {
            Coordinate2D coordinate2D = p.getKey();
            fields.get(coordinate2D.y()).set(coordinate2D.x(), "@");
        }
        List<DisplayData> result = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            result.add(new DisplayData(new Coordinate2D(0, i) ,fields.get(i)));
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public List<DisplayData> getFields() {
        return this.changeDisplay();
    }

    public boolean isObject(Coordinate2D position) {
        return this.objects.containsKey(position);
    }

    public boolean isGold(Coordinate2D position) {
        return this.golds.containsKey(position);
    }

    public ThingSimple getObject(Coordinate2D position) {
        return this.objects.get(position);
    }

    public Gold getGold(Coordinate2D position) {
        return this.golds.get(position);
    }

    public void removeObject(Coordinate2D position) {
        this.objects.remove(position);
    }

    public Gold removeGold(Coordinate2D position) {
        return this.golds.remove(position);
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

    public void setObjects(List<ThingSimple> thingList) {
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
                .map(it -> new ObjectCoordinateDomain(it.getKey(), it.getValue().instanceId()))
                .collect(Collectors.toList());
    }

    public List<GoldCoordinateDomain> getSymbolGolds() {
        return this.golds.entrySet().stream()
                .map(it -> new GoldCoordinateDomain(it.getKey(), it.getValue().getGold()))
                .collect(Collectors.toList());
    }
}
