package net.hero.rogueb.services;

import net.hero.rogueb.bag.Bag;
import net.hero.rogueb.bookofadventureclient.BookOfAdventureServiceClient;
import net.hero.rogueb.bookofadventureclient.o.LocationDto;
import net.hero.rogueb.bookofadventureclient.o.PlayerDto;
import net.hero.rogueb.dungeonclient.DungeonServiceClient;
import net.hero.rogueb.dungeonclient.o.DungeonLocation;
import net.hero.rogueb.dungeonclient.o.MoveEnum;
import net.hero.rogueb.objectclient.ObjectServiceClient;
import net.hero.rogueb.worldclient.WorldServiceClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.Map;

@Service
public class PlayerService {
    private final WorldServiceClient worldServiceClient;
    private final BookOfAdventureServiceClient bookOfAdventureServiceClient;
    private final DungeonServiceClient dungeonServiceClient;
    private final ObjectServiceClient objectServiceClient;

    public PlayerService(WorldServiceClient worldServiceClient,
                         BookOfAdventureServiceClient bookOfAdventureServiceClient,
                         DungeonServiceClient dungeonServiceClient,
                         ObjectServiceClient objectServiceClient) {
        this.worldServiceClient = worldServiceClient;
        this.bookOfAdventureServiceClient = bookOfAdventureServiceClient;
        this.dungeonServiceClient = dungeonServiceClient;
        this.objectServiceClient = objectServiceClient;
    }

    public Mono<Map<String, String>> gotoDungeon(int userId) {
        Mono<PlayerDto> playerDtoMono = this.bookOfAdventureServiceClient.getPlayer(userId);
        Mono<LocationDto> locationDtoMono = this.worldServiceClient.getStartDungeon()
                .flatMap(dungeonInfo1 -> this.dungeonServiceClient.gotoDungeon(dungeonInfo1.id(), userId))
                .map(this::createLocationDto);
        return Mono.zip(playerDtoMono, locationDtoMono)
                .doOnNext(tuple -> tuple.getT1().setLocationDto(tuple.getT2()))
                .map(Tuple2::getT1)
                .flatMap(this.bookOfAdventureServiceClient::save)
                .map(it -> Map.of("key", "Hello World"));
    }

    public Mono<Map<String, Boolean>> move(int userId, MoveEnum moveEnum) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .flatMap(playerDto1 -> Mono.zip(Mono.just(playerDto1),
                        this.dungeonServiceClient.move(playerDto1.getLocationDto().getDungeonId(),
                                userId,
                                playerDto1.getLocationDto().getLevel(),
                                playerDto1.getLocationDto().getX(),
                                playerDto1.getLocationDto().getY(),
                                moveEnum)))
                .map(tuple2 -> {
                    tuple2.getT1().getLocationDto().setX(tuple2.getT2().x());
                    tuple2.getT1().getLocationDto().setY(tuple2.getT2().y());
                    return tuple2.getT1();
                })
                .flatMap(this.bookOfAdventureServiceClient::save)
                .map(it -> Map.of("result", true));
    }

    public Mono<Map<String, Object>> pickup(int userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .flatMap(playerDto -> Mono.zip(Mono.just(playerDto),
                        this.dungeonServiceClient.whatIsOnMyFeet(playerDto.getLocationDto().getDungeonId(),
                                userId,
                                playerDto.getLocationDto().getLevel(),
                                playerDto.getLocationDto().getX(),
                                playerDto.getLocationDto().getX())))
                .flatMap(tuple -> switch (tuple.getT2()) {
                    case None -> Mono.just(Map.of("result", false, "message", "NoObjectOnTheFloor"));
                    case Gold -> this.pickupGold(tuple.getT1());
                    case Object -> this.pickupObject(tuple.getT1());
                });
    }

    private Mono<Map<String, Object>> pickupGold(PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocationDto();
        return this.dungeonServiceClient.pickUpGold(locationDto.getDungeonId(),
                playerDto.getId(), locationDto.getLevel(), locationDto.getX(), locationDto.getY())
                .doOnNext(gold -> playerDto.setGold(playerDto.getGold() + gold.gold()))
                .doOnNext(gold -> this.bookOfAdventureServiceClient.save(playerDto))
                .map(gold -> Map.<String, Object>of("result", true, "type", 1, "gold", gold.gold()))
                .onErrorResume(it -> Mono.just(Map.of("result", false, "message", "NoObjectOnTheFloor")));
    }


    private Mono<Map<String, Object>> pickupObject(PlayerDto playerDto) {
        return this.bookOfAdventureServiceClient.getItemList(playerDto.getId())
                .flatMap(this.objectServiceClient::getObjects)
                .map(thingMap -> {
                    Bag bag = new Bag();
                    bag.setContents(new ArrayList<>(thingMap.values()));
                    return bag;
                })
                .flatMap(bag -> Mono.zip(Mono.just(bag),
                        this.dungeonServiceClient.pickUpObject(playerDto.getLocationDto().getDungeonId(),
                                playerDto.getId(),
                                playerDto.getLocationDto().getLevel(),
                                playerDto.getLocationDto().getX(),
                                playerDto.getLocationDto().getY())))
                .flatMap(tuple2 -> pickUpObjectExecute(playerDto, tuple2));
    }

    private Mono<Map<String, Object>> pickUpObjectExecute(PlayerDto playerDto, Tuple2<Bag, Integer> t) {
        if (t.getT1().getEmptySize() <= 0) {
            return Mono.just(Map.of("result", false, "message", "BagNoEmpty"));
        } else if (t.getT2() == 0) {
            return Mono.just(Map.of("result", false, "message", "NoObjectOnTheFloor"));
        } else {
            t.getT1().getThingIdList().add(t.getT2());
            return this.bookOfAdventureServiceClient.changeObject(playerDto.getId(), t.getT1().getThingIdList())
                    .flatMap(it -> this.objectServiceClient.getObjectInfo(t.getT2()))
                    .map(thingInfo -> Map.of("result", true, "type", 2, "itemName", thingInfo.name()));
        }
    }

    public Mono<Map<String, Boolean>> downStairs(int userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .flatMap(playerDto -> Mono.zip(Mono.just(playerDto),
                        this.dungeonServiceClient.downStairs(playerDto.getLocationDto().getDungeonId(),
                                userId,
                                playerDto.getLocationDto().getLevel(),
                                playerDto.getLocationDto().getX(),
                                playerDto.getLocationDto().getY())))
                .map(tuple -> {
                    tuple.getT1().setLocationDto(this.createLocationDto(tuple.getT2()));
                    return tuple.getT1();
                })
                .flatMap(this.bookOfAdventureServiceClient::save)
                .map(it -> Map.of("result", true));
    }

    public Mono<Map<String, Boolean>> upStairs(int userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .flatMap(playerDto -> Mono.zip(Mono.just(playerDto),
                        this.dungeonServiceClient.upStairs(playerDto.getLocationDto().getDungeonId(),
                                userId,
                                playerDto.getLocationDto().getLevel(),
                                playerDto.getLocationDto().getX(),
                                playerDto.getLocationDto().getY())))
                .map(tuple -> {
                    tuple.getT1().setLocationDto(this.createLocationDto(tuple.getT2()));
                    return tuple.getT1();
                })
                .flatMap(this.bookOfAdventureServiceClient::save)
                .map(it -> Map.of("result", true));
    }

    private LocationDto createLocationDto(DungeonLocation dungeonLocation) {
        LocationDto locationDto = new LocationDto();
        locationDto.setDungeonId(dungeonLocation.dungeonId());
        locationDto.setLevel(dungeonLocation.level());
        locationDto.setX(dungeonLocation.coordinate2D().x());
        locationDto.setY(dungeonLocation.coordinate2D().y());
        return locationDto;
    }

    public Mono<Boolean> existPlayer(String userName) {
        return this.bookOfAdventureServiceClient.exist(userName);
    }

    public Mono<Integer> create(String userName) {
        return this.bookOfAdventureServiceClient.create(userName);
    }
}
