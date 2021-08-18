package net.hero.rogueb.dungeon;

import net.hero.rogueb.dungeon.domain.DungeonDomain;
import net.hero.rogueb.dungeon.domain.DungeonPlayerDomain;
import net.hero.rogueb.dungeon.domain.FloorDomain;
import net.hero.rogueb.dungeon.domain.ObjectCoordinateDomain;
import net.hero.rogueb.dungeon.fields.Coordinate2D;
import net.hero.rogueb.dungeon.fields.DisplayData;
import net.hero.rogueb.dungeon.fields.DungeonLocation;
import net.hero.rogueb.dungeon.fields.Floor;
import net.hero.rogueb.dungeon.fields.Gold;
import net.hero.rogueb.dungeon.base.o.ThingOverviewType;
import net.hero.rogueb.dungeon.repositories.DungeonPlayerRepository;
import net.hero.rogueb.dungeon.repositories.DungeonRepository;
import net.hero.rogueb.dungeon.repositories.FloorRepository;
import net.hero.rogueb.objectclient.ObjectServiceClient;
import net.hero.rogueb.objectclient.o.ThingSimple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Transactional
@Service
public class DungeonService {
    private final DungeonRepository dungeonRepository;
    private final DungeonPlayerRepository dungeonPlayerRepository;
    private final FloorRepository floorRepository;
    private final ObjectServiceClient objectServiceClient;

    public DungeonService(DungeonRepository dungeonRepository,
                          DungeonPlayerRepository dungeonPlayerRepository,
                          FloorRepository floorRepository,
                          ObjectServiceClient objectServiceClient) {
        this.dungeonRepository = dungeonRepository;
        this.dungeonPlayerRepository = dungeonPlayerRepository;
        this.floorRepository = floorRepository;
        this.objectServiceClient = objectServiceClient;
    }

    public Mono<DungeonDomain> findByName(String name) {
        return this.dungeonRepository.findByName(name)
                .switchIfEmpty(Flux.just(new DungeonDomain()))
                .elementAt(0);
    }

    public Mono<String> save(String name) {
        return this.dungeonRepository.save(new DungeonDomain(name, 1, "localhost", 3))
                .map(DungeonDomain::getId);
    }


    public Flux<DisplayData> displayData(DungeonLocation location) {
        return this.getFloor(location)
                .doOnNext(floor -> floor.move(location.getCoordinate2D(), location.getPlayerId()))
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
                .map(dungeonDomain -> new Floor(level, dungeonDomain))
                .flatMap(floor -> Mono.zip(
                        Mono.just(floor),
                        this.objectServiceClient.createObjects(floor.getItemCreateCount()).collectList()))
                .flatMap(tuple2 -> {
                    tuple2.getT1().setObjects(tuple2.getT2());
                    return this.saveFloor(tuple2.getT1(), playerId);
                });
    }

    private Mono<DungeonLocation> up(String dungeonId, int level, String playerId) {
        return this.floorRepository.findByDungeonIdAndLevelAndUserId(dungeonId, level, playerId)
                .elementAt(0)
                .map(floorDomain -> new DungeonLocation(dungeonId, playerId, level, floorDomain.getDownStairs()))
                .switchIfEmpty(Mono.error(new RuntimeException("Data不整合")));
    }

    public Mono<Coordinate2D> move(DungeonLocation location, int toX, int toY) {
        return this.getFloor(location)
                .filter(f -> f.getFields().get(toY).data().get(toX).equals("#"))
                .map(it -> location.getCoordinate2D())
                .switchIfEmpty(Mono.just(new Coordinate2D(toX, toY)));
    }

    public Mono<DungeonLocation> downStairs(DungeonLocation location) {
        return this.dungeonRepository.findById(location.getDungeonId())
                .filter(dungeonDomain -> dungeonDomain.getMaxLevel() > location.getLevel())
                .flatMap(dungeonDomain -> this.getFloor(location))
                .filter(floor -> floor.getDownStairs().equals(location.getCoordinate2D()))
                .flatMap(floor -> this.down(location.getDungeonId(), location.getLevel() + 1, location.getPlayerId()))
                .switchIfEmpty(Mono.just(location));
    }

    public Mono<DungeonLocation> upStairs(DungeonLocation location) {
        if (location.getLevel() == 1) {
            // TODO:暫定対応
            return Mono.just(location);
        }

        return this.getFloor(location)
                .filter(floor -> floor.getUpStairs().equals(location.getCoordinate2D()))
                .flatMap(it -> this.up(location.getDungeonId(), location.getLevel() - 1, location.getPlayerId()))
                .switchIfEmpty(Mono.just(location));
    }

    public Mono<Integer> pickUpObject(DungeonLocation location) {
        return this.getFloor(location).filter(floor -> floor.isObject(location.getCoordinate2D()))
                .map(floor -> {
                    ThingSimple thingSimple = floor.getObject(location.getCoordinate2D());
                    floor.removeObject(location.getCoordinate2D());
                    this.saveFloor(floor, location.getPlayerId()).block();
                    return thingSimple;
                })
                .map(ThingSimple::id)
                .switchIfEmpty(Mono.just(0));
    }

    public Mono<Gold> pickUpGold(DungeonLocation location) {
        return this.getFloor(location)
                .filter(floor -> floor.isGold(location.getCoordinate2D()))
                .map(floor -> {
                    Gold gold = floor.removeGold(location.getCoordinate2D());
                    this.saveFloor(floor, location.getPlayerId()).block();
                    return gold;
                })
                .switchIfEmpty(Mono.just(new Gold(0)));
    }


    public Mono<ThingOverviewType> whatIsOnMyFeet(DungeonLocation dungeonLocation) {
        return this.getFloor(dungeonLocation)
                .map(floor -> {
                    if (floor.isGold(dungeonLocation.getCoordinate2D())) {
                        return ThingOverviewType.Gold;
                    } else if (floor.isObject(dungeonLocation.getCoordinate2D())) {
                        return ThingOverviewType.Object;
                    }
                    return ThingOverviewType.None;
                });
    }

    private Mono<Floor> getFloor(DungeonLocation location) {
        return this.floorRepository.findByDungeonIdAndLevelAndUserId(location.getDungeonId(), location.getLevel(), location.getPlayerId())
                .elementAt(0)
                .flatMap(floorDomain -> Mono.zip(
                        Mono.just(floorDomain),
                        this.objectServiceClient.getObjects(floorDomain.getThingList().stream()
                                .map(ObjectCoordinateDomain::getObjectId)
                                .collect(Collectors.toList()))))
                .map(t -> new Floor(t.getT1(), t.getT2()));
    }

    private Mono<FloorDomain> saveFloor(Floor floor, String playerId) {
        FloorDomain floorDomain = new FloorDomain();
        floorDomain.setId(floor.getId());
        floorDomain.setDungeonId(floor.getDungeonId());
        floorDomain.setLevel(floor.getLevel());
        floorDomain.setUserId(playerId);
        floorDomain.setDownStairs(floor.getDownStairs());
        floorDomain.setUpStairs(floor.getUpStairs());
        floorDomain.setThingList(floor.getSymbolObjects());
        floorDomain.setGoldList(floor.getSymbolGolds());

        return this.floorRepository.save(floorDomain);
    }
}
