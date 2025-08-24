package net.hero.rogueb.dungeon;

import net.hero.rogueb.dungeon.base.o.ThingOverviewType;
import net.hero.rogueb.dungeon.domain.DungeonDomain;
import net.hero.rogueb.dungeon.domain.DungeonPlayerDomain;
import net.hero.rogueb.dungeon.domain.FloorDomain;
import net.hero.rogueb.dungeon.domain.ObjectCoordinateDomain;
import net.hero.rogueb.dungeon.fields.Coordinate;
import net.hero.rogueb.dungeon.fields.DisplayData;
import net.hero.rogueb.dungeon.fields.DungeonLocation;
import net.hero.rogueb.dungeon.fields.Floor;
import net.hero.rogueb.dungeon.fields.Gold;
import net.hero.rogueb.dungeon.fields.factories.AbstractFactory;
import net.hero.rogueb.dungeon.fields.factories.D2StringFactory;
import net.hero.rogueb.dungeon.repositories.DungeonPlayerRepository;
import net.hero.rogueb.dungeon.repositories.DungeonRepository;
import net.hero.rogueb.dungeon.repositories.FloorRepository;
import net.hero.rogueb.math.Random;
import net.hero.rogueb.objectclient.ObjectServiceClient;
import net.hero.rogueb.objectclient.o.ThingSimple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class DungeonService {
    private final DungeonRepository dungeonRepository;
    private final DungeonPlayerRepository dungeonPlayerRepository;
    private final FloorRepository floorRepository;
    private final ObjectServiceClient objectServiceClient;
    private final AbstractFactory<String> factory;

    public DungeonService(DungeonRepository dungeonRepository,
                          DungeonPlayerRepository dungeonPlayerRepository,
                          FloorRepository floorRepository,
                          ObjectServiceClient objectServiceClient) {
        this.dungeonRepository = dungeonRepository;
        this.dungeonPlayerRepository = dungeonPlayerRepository;
        this.floorRepository = floorRepository;
        this.objectServiceClient = objectServiceClient;
        this.factory = new D2StringFactory();
    }

    public Mono<DungeonDomain> findByName(String name) {
        return this.dungeonRepository.findByName(name)
                .switchIfEmpty(Flux.just(new DungeonDomain()))
                .elementAt(0);
    }

    public Mono<String> save(String name) {
        return this.dungeonRepository.save(new DungeonDomain(name, 1, "localhost", 3, 5))
                .map(DungeonDomain::getId);
    }


    public Flux<DisplayData<String>> displayData(DungeonLocation location) {
        return this.getFloor(location)
                .doOnNext(floor -> floor.move(location.getCoordinate(), location.getPlayerId()))
                .flatMapIterable(Floor::getFields);
    }

    public Mono<String> getDungeonName(String dungeonId) {
        return this.dungeonRepository.findById(dungeonId)
                .map(DungeonDomain::getName);
    }

    public Mono<DungeonLocation> gotoDungeon(String dungeonId, String playerId) {
        return this.down(dungeonId, 1, playerId);
    }

    private Mono<DungeonLocation> down(String dungeonId, int level, String playerId) {
        return this.dungeonPlayerRepository.deleteDungeonPlayerDomainsByDungeonIdAndPlayerId(dungeonId, playerId)
                .then(this.dungeonPlayerRepository.save(new DungeonPlayerDomain(dungeonId, playerId, level)))
                .then(this.floorRepository.findByDungeonIdAndLevelAndUserId(dungeonId, level, playerId)
                        .switchIfEmpty(createNewLevel(dungeonId, level, playerId))
                        .elementAt(0))
                .map(FloorDomain::getUpStairs)
                .map(coordinate -> new DungeonLocation(dungeonId, playerId, level, coordinate));
    }

    private Mono<FloorDomain> createNewLevel(String dungeonId, int level, String playerId) {
        return this.dungeonRepository.findById(dungeonId)
                .flatMap(dungeonDomain -> Mono.zip(Mono.just(dungeonDomain),
                        this.objectServiceClient.createObjects(Random.rnd(dungeonDomain.getItemSeed()), this.createMessageForDungeon(dungeonDomain, level)).collectList()))
                .map(tuple2 -> new Floor<>(level, tuple2.getT1(), tuple2.getT2(), factory))
                .flatMap(f -> this.saveFloor(f, playerId));
    }

    private String createMessageForDungeon(DungeonDomain dungeonDomain, int level) {
        return "Dungeon: '" + dungeonDomain.getName() + "' Level: '" + level + "'に作成。";
    }

    private Mono<DungeonLocation> up(String dungeonId, int level, String playerId) {
        return this.floorRepository.findByDungeonIdAndLevelAndUserId(dungeonId, level, playerId)
                .elementAt(0)
                .map(floorDomain -> new DungeonLocation(dungeonId, playerId, level, floorDomain.getDownStairs()))
                .switchIfEmpty(Mono.error(new RuntimeException("Data不整合")));
    }

    public Mono<Coordinate> move(DungeonLocation location, int toX, int toY) {
        return this.getFloor(location)
                .filter(f -> f.getFields().get(toY).data().get(toX).equals("#"))
                .map(it -> location.getCoordinate())
                .switchIfEmpty(Mono.just(this.factory.createCoordinate(List.of(toX, toY))));
    }

    public Mono<DungeonLocation> downStairs(DungeonLocation location) {
        return this.dungeonRepository.findById(location.getDungeonId())
                .filter(dungeonDomain -> dungeonDomain.getMaxLevel() > location.getLevel())
                .flatMap(dungeonDomain -> this.getFloor(location))
                .filter(floor -> floor.getDownStairs().isPresent())
                .filter(floor -> floor.getDownStairs().get().equals(location.getCoordinate()))
                .flatMap(floor -> this.down(location.getDungeonId(), location.getLevel() + 1, location.getPlayerId()))
                .switchIfEmpty(Mono.just(location));
    }

    public Mono<DungeonLocation> upStairs(DungeonLocation location) {
        if (location.getLevel() == 1) {
            // TODO:暫定対応
            return Mono.just(location);
        }

        return this.getFloor(location)
                .filter(floor -> floor.getUpStairs().isPresent())
                .filter(floor -> floor.getUpStairs().get().equals(location.getCoordinate()))
                .flatMap(it -> this.up(location.getDungeonId(), location.getLevel() - 1, location.getPlayerId()))
                .switchIfEmpty(Mono.just(location));
    }

    public Mono<String> pickUpObject(DungeonLocation location) {
        return this.getFloor(location).filter(floor -> floor.isObject(location.getCoordinate()))
                .flatMap(floor -> {
                    floor.removeObject(location.getCoordinate());
                    return Mono.zip(Mono.just(floor), this.saveFloor(floor, location.getPlayerId()));
                })
                .map(it -> it.getT1().getObject(location.getCoordinate()))
                .map(ThingSimple::instanceId)
                .switchIfEmpty(Mono.just(""));
    }

    public Mono<Gold> pickUpGold(DungeonLocation location) {
        return this.getFloor(location)
                .filter(floor -> floor.isGold(location.getCoordinate()))
                .flatMap(floor -> {
                    Gold gold = floor.removeGold(location.getCoordinate());
                    return Mono.zip(Mono.just(gold), this.saveFloor(floor, location.getPlayerId()));
                })
                .map(Tuple2::getT1)
                .switchIfEmpty(Mono.just(new Gold(0)));
    }


    public Mono<ThingOverviewType> whatIsOnMyFeet(DungeonLocation dungeonLocation) {
        return this.getFloor(dungeonLocation)
                .map(floor -> {
                    if (floor.isGold(dungeonLocation.getCoordinate())) {
                        return ThingOverviewType.Gold;
                    } else if (floor.isObject(dungeonLocation.getCoordinate())) {
                        return ThingOverviewType.Object;
                    }
                    return ThingOverviewType.None;
                });
    }

    private Mono<Floor<String>> getFloor(DungeonLocation location) {
        return this.floorRepository.findByDungeonIdAndLevelAndUserId(location.getDungeonId(), location.getLevel(), location.getPlayerId())
                .elementAt(0)
                .flatMap(floorDomain -> Mono.zip(
                        Mono.just(floorDomain),
                        this.objectServiceClient.getObjects(floorDomain.getThingList().stream()
                                .map(ObjectCoordinateDomain::getObjectId)
                                .collect(Collectors.toList()))))
                .map(t -> new Floor<>(t.getT1(), t.getT2(), factory));
    }

    private Mono<FloorDomain> saveFloor(Floor<String> floor, String playerId) {
        FloorDomain floorDomain = new FloorDomain();
        floorDomain.setId(floor.getId());
        floorDomain.setDungeonId(floor.getDungeonId());
        floorDomain.setLevel(floor.getLevel());
        floorDomain.setUserId(playerId);
        if (floor.getDownStairs().isPresent()) {
            floorDomain.setDownStairs(floor.getDownStairs().get());
        }
        if (floor.getUpStairs().isPresent()) {
            floorDomain.setUpStairs(floor.getUpStairs().get());
        }
        floorDomain.setThingList(floor.getSymbolObjects());
        floorDomain.setGoldList(floor.getSymbolGolds());
        floorDomain.setTiles(floor.getTiles());

        return this.floorRepository.save(floorDomain);
    }
}
