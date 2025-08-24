package net.hero.rogueb.dungeon.fields;

import net.hero.rogueb.dungeon.domain.DungeonDomain;
import net.hero.rogueb.dungeon.domain.FloorDomain;
import net.hero.rogueb.dungeon.domain.GoldCoordinateDomain;
import net.hero.rogueb.dungeon.domain.ObjectCoordinateDomain;
import net.hero.rogueb.dungeon.fields.factories.AbstractFactory;
import net.hero.rogueb.math.Random;
import net.hero.rogueb.objectclient.o.ThingSimple;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Floor<T> {

    private final String id;
    private final String dungeonId;
    /**
     * 階層数
     */
    private final int level;
    private final Coordinate size;
    // private final List<Space<T>> spaces;
    private final List<List<Tile<T>>> tiles;
    private final Optional<Coordinate> downStairs;
    private final Optional<Coordinate> upStairs;
    private final Map<Coordinate, String> playerMap;
    private final Map<Coordinate, ThingSimple> objects;
    private final Map<Coordinate, Gold> golds;
    private final AbstractFactory<T> factory;

    /**
     * 新規作成時
     *
     * @param level         階層数
     * @param dungeonDomain DungeonDomain
     */
    public Floor(int level, DungeonDomain dungeonDomain, List<ThingSimple> objectList, AbstractFactory<T> factory) {
        this.id = null;
        this.dungeonId = dungeonDomain.getId();
        this.level = level;
        this.playerMap = new HashMap<>();
        this.objects = new HashMap<>();
        this.golds = new HashMap<>();
        this.factory = factory;
        this.size = this.factory.createCoordinate(List.of(80, 40));
        this.tiles = new LinkedList<>();

        List<Space<T>> spaces = this.createSpaces(objectList.size(), dungeonDomain.getRoomCountSeed(), level != dungeonDomain.getMaxLevel());

        // 値の設定
        this.upStairs = spaces.stream().filter(it -> it.getUpStairs().isPresent())
                .findFirst().map(it -> it.getUpStairs().get());
        this.downStairs = spaces.stream().filter(it -> it.getDownStairs().isPresent())
                .findFirst().map(it -> it.getDownStairs().get());
        spaces.stream().flatMap(it -> it.getGoldCoordinates().stream())
                .forEach(c -> this.golds.put(c, new Gold(Random.rnd(level * 10 + 5))));
        Iterator<ThingSimple> iterator = objectList.iterator();
        spaces.stream().flatMap(it -> it.getThingCoordinates().stream())
                .forEach(it -> this.objects.put(it, iterator.next()));
    }

    /**
     * ロード時
     *
     * @param floorDomain 保存したデータ
     * @param thingMap    ObjectIdがkeyとなっているThingSimpleのMap
     */
    public Floor(FloorDomain floorDomain, Map<String, ThingSimple> thingMap, AbstractFactory<T> factory) {
        this.id = floorDomain.getId();
        this.dungeonId = floorDomain.getDungeonId();
        this.level = floorDomain.getLevel();
        this.playerMap = new HashMap<>();
        this.objects = new HashMap<>();
        this.golds = new HashMap<>();
        this.factory = factory;
        this.size = factory.createCoordinate(List.of(80, 40));
        for (var thing : floorDomain.getThingList()) {
            this.objects.put(thing.getPosition(), thingMap.get(thing.getObjectId()));
        }
        for (var gold : floorDomain.getGoldList()) {
            this.golds.put(gold.getPosition(), new Gold(gold.getGold()));
        }
        this.upStairs = Optional.ofNullable(floorDomain.getUpStairs());
        this.downStairs = Optional.ofNullable(floorDomain.getDownStairs());
        this.tiles = floorDomain.getTiles().stream()
                .map(it -> it.stream().map(this.factory::convertTile).toList())
                .toList();
    }

    private List<Space<T>> createSpaces(int maxItemCount, int roomCountSeed, boolean downStairsFlag) {
        int roomCount = Random.rnd(roomCountSeed);

        // 部屋分け
        record RoomCoordinate(Coordinate position, Coordinate size) {
            long getArea() {
                return RoomCoordinate.this.size.area();
            }
        }
        List<RoomCoordinate> roomCoordinateList = new LinkedList<>();
        roomCoordinateList.add(new RoomCoordinate(this.factory.createCoordinate(List.of(0, 0)), this.size));

        for (int i = 0; i < roomCount; i++) {
            // 縦横の判断と閾値
            Optional<RoomCoordinate> max = roomCoordinateList.stream().reduce((c1, c2) -> {
                if (c1.getArea() > c2.getArea()) {
                    return c1;
                }
                return c2;
            });
            RoomCoordinate roomCoordinate = max.orElseThrow();
            roomCoordinateList.remove(roomCoordinate);
            Coordinate size = roomCoordinate.size();
            if (size.x() > size.y()) {
                if (size.x() < 6) {
                    roomCoordinateList.add(roomCoordinate);
                    break;
                }
                int splitX = Random.rnd(size.x() - 5) + 5;
                Coordinate splitCoordinate = this.factory.createCoordinate(List.of(splitX, 0));
                roomCoordinateList.add(new RoomCoordinate(roomCoordinate.position(),
                        this.factory.createCoordinate(List.of(splitX, size.y()))));
                roomCoordinateList.add(new RoomCoordinate(roomCoordinate.position().plus(splitCoordinate),
                        size.minus(splitCoordinate)));
            } else {
                if (size.y() < 6) {
                    roomCoordinateList.add(roomCoordinate);
                    break;
                }
                int splitY = Random.rnd(size.y() - 5) + 5;
                Coordinate splitCoordinate = this.factory.createCoordinate(List.of(0, splitY));
                roomCoordinateList.add(new RoomCoordinate(roomCoordinate.position(),
                        this.factory.createCoordinate(List.of(size.x(), splitY))));
                roomCoordinateList.add(new RoomCoordinate(roomCoordinate.position().plus(splitCoordinate),
                        size.minus(splitCoordinate)));
            }
        }

        // 部屋付属品作成
        int size = roomCoordinateList.size();
        // mutable flags list
        List<Boolean> upStairsFlags = new LinkedList<>(IntStream.range(0, size)
                .mapToObj(it -> Boolean.FALSE)
                .toList());
        upStairsFlags.set(Random.rnd(size), Boolean.TRUE);
        List<Boolean> downStairsFlags = new LinkedList<>(IntStream.range(0, size)
                .mapToObj(it -> Boolean.FALSE)
                .toList());
        if (downStairsFlag && size > 0) {
            downStairsFlags.set(Random.rnd(size), Boolean.TRUE);
        }
        List<Integer> itemCountList = new LinkedList<>();
        int itemCount = Math.max(0, maxItemCount);
        for (int i = 1; i < size; i++) {
            int roomItemCount = itemCount > 0 ? Random.rnd(itemCount) : 0;
            itemCount = itemCount - roomItemCount;
            itemCountList.add(roomItemCount);
        }
        itemCountList.add(itemCount);

        // initialize full-floor tiles with walls
        this.tiles.clear();
        for (int y = 0; y < this.size.y(); y++) {
            List<Tile<T>> row = new LinkedList<>();
            for (int x = 0; x < this.size.x(); x++) {
                row.add(this.factory.createPoint(net.hero.rogueb.dungeon.base.o.PointType.Wall));
            }
            this.tiles.add(row);
        }

        // 部屋作成と床の掘削（Spaceは座標の提供に使用）
        List<Space<T>> spaces = new LinkedList<>();
        List<Coordinate> roomCenters = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            var rc = roomCoordinateList.get(i);
            // carve inner room area: start+2, size-4 to align with Room/Space logic
            Coordinate innerPos = this.factory.createCoordinate(List.of(rc.position().x() + 2, rc.position().y() + 2));
            Coordinate innerSize = this.factory.createCoordinate(List.of(Math.max(0, rc.size().x() - 4), Math.max(0, rc.size().y() - 4)));
            for (int yy = 0; yy < innerSize.y(); yy++) {
                int y = innerPos.y() + yy;
                if (y < 0 || y >= this.size.y()) continue;
                for (int xx = 0; xx < innerSize.x(); xx++) {
                    int x = innerPos.x() + xx;
                    if (x < 0 || x >= this.size.x()) continue;
                    this.tiles.get(y).set(x, this.factory.createPoint(net.hero.rogueb.dungeon.base.o.PointType.floor));
                }
            }
            // center for corridor connection (clamp to bounds)
            int cx = Math.min(this.size.x() - 1, Math.max(0, innerPos.x() + Math.max(0, innerSize.x() - 1) / 2));
            int cy = Math.min(this.size.y() - 1, Math.max(0, innerPos.y() + Math.max(0, innerSize.y() - 1) / 2));
            roomCenters.add(this.factory.createCoordinate(List.of(cx, cy)));

            Space<T> e = new Space<>(downStairsFlags.get(i), upStairsFlags.get(i), rc.position(), rc.size(), 1, itemCountList.get(i), this.factory);
            spaces.add(e);
        }

        // 部屋繋ぎ：隣接する部屋の中心を直交経路で接続
        for (int i = 0; i + 1 < roomCenters.size(); i++) {
            Coordinate a = roomCenters.get(i);
            Coordinate b = roomCenters.get(i + 1);
            int x = a.x();
            int y = a.y();
            // horizontal tunnel
            int stepX = (b.x() >= x) ? 1 : -1;
            while (x != b.x()) {
                if (y >= 0 && y < this.size.y() && x >= 0 && x < this.size.x()) {
                    this.tiles.get(y).set(x, this.factory.createPoint(net.hero.rogueb.dungeon.base.o.PointType.floor));
                }
                x += stepX;
            }
            // vertical tunnel
            int stepY = (b.y() >= y) ? 1 : -1;
            while (y != b.y()) {
                if (y >= 0 && y < this.size.y() && x >= 0 && x < this.size.x()) {
                    this.tiles.get(y).set(x, this.factory.createPoint(net.hero.rogueb.dungeon.base.o.PointType.floor));
                }
                y += stepY;
            }
            // ensure destination cell is floor
            if (y >= 0 && y < this.size.y() && x >= 0 && x < this.size.x()) {
                this.tiles.get(y).set(x, this.factory.createPoint(net.hero.rogueb.dungeon.base.o.PointType.floor));
            }
        }

        // 階段の配置（表示用タイルを設定）
        for (Space<T> sp : spaces) {
            sp.getUpStairs().ifPresent(c -> {
                if (c.y() >= 0 && c.y() < this.size.y() && c.x() >= 0 && c.x() < this.size.x()) {
                    this.tiles.get(c.y()).set(c.x(), this.factory.createPoint(net.hero.rogueb.dungeon.base.o.PointType.UpStairs));
                }
            });
            sp.getDownStairs().ifPresent(c -> {
                if (c.y() >= 0 && c.y() < this.size.y() && c.x() >= 0 && c.x() < this.size.x()) {
                    this.tiles.get(c.y()).set(c.x(), this.factory.createPoint(net.hero.rogueb.dungeon.base.o.PointType.downStairs));
                }
            });
        }

        return spaces;
    }

    public Coordinate enterFromUpStairs(String playerId) {
        Coordinate coordinate = this.getUpStairs().orElseThrow();
        this.playerMap.put(coordinate, playerId);
        return coordinate;
    }

    public Coordinate move(Coordinate coordinate, String playerId) {
        this.playerMap.put(coordinate, playerId);
        return coordinate;
    }

    private List<DisplayData<T>> changeDisplay() {
        List<List<T>> tiles = this.tiles.stream().map(it -> it.stream().map(Tile::display).toList()).toList();
        for (var s : this.objects.entrySet()) {
            Coordinate coordinate = s.getKey();
            tiles.get(coordinate.y()).set(coordinate.x(), this.factory.getDisplay(s.getValue()));
        }
        for (var g : this.golds.entrySet()) {
            Coordinate coordinate = g.getKey();
            tiles.get(coordinate.y()).set(coordinate.x(), this.factory.getDisplay(g.getValue()));
        }
        for (var p : this.playerMap.entrySet()) {
            Coordinate coordinate = p.getKey();
            tiles.get(coordinate.y()).set(coordinate.x(), this.factory.getPlayerDisplay());
        }

        return IntStream.range(0, this.size.y())
                .mapToObj(y -> new DisplayData<T>(this.factory.createCoordinate(List.of(0, y)), tiles.get(y)))
                .toList();
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

    public List<List<Tile<T>>> getTiles() {
        return tiles;
    }

    public List<DisplayData<T>> getFields() {
        return this.changeDisplay();
    }

    public boolean isObject(Coordinate position) {
        return this.objects.containsKey(position);
    }

    public boolean isGold(Coordinate position) {
        return this.golds.containsKey(position);
    }

    public ThingSimple getObject(Coordinate position) {
        return this.objects.get(position);
    }

    public Gold getGold(Coordinate position) {
        return this.golds.get(position);
    }

    public void removeObject(Coordinate position) {
        this.objects.remove(position);
    }

    public Gold removeGold(Coordinate position) {
        return this.golds.remove(position);
    }

    public Optional<Coordinate> getUpStairs() {
        return this.upStairs;
    }

    public Optional<Coordinate> getDownStairs() {
        return this.downStairs;
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
