package net.hero.rogueb.services;

import net.hero.rogueb.bag.Bag;
import net.hero.rogueb.bookofadventureclient.BookOfAdventureServiceClient;
import net.hero.rogueb.bookofadventureclient.o.PlayerDto;
import net.hero.rogueb.converts.Convert;
import net.hero.rogueb.dungeonclient.DungeonServiceClient;
import net.hero.rogueb.dungeonclient.o.MoveEnum;
import net.hero.rogueb.math.ReproducibleRandom;
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

    public Mono<Map<String, String>> gotoDungeon(String userId) {
        Mono<PlayerDto> playerDtoMono = this.bookOfAdventureServiceClient.getPlayer(userId);
        Mono<Map<String, Object>> locationDtoMono = this.worldServiceClient.getStartDungeon()
                .flatMap(dungeonInfo -> this.dungeonServiceClient.gotoDungeon(dungeonInfo.id(), userId))
                .map(Convert::dungeonLocation2PlayerLocation);
        return Mono.zip(playerDtoMono, locationDtoMono)
                .doOnNext(tuple -> tuple.getT1().setLocation(tuple.getT2()))
                .map(Tuple2::getT1)
                .flatMap(this.bookOfAdventureServiceClient::save)
                .map(it -> Map.of("key", "Hello World"));
    }

    public Mono<Map<String, Boolean>> move(String userId, MoveEnum moveEnum) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .flatMap(playerDto1 -> Mono.zip(Mono.just(playerDto1),
                        this.dungeonServiceClient.move(Convert.playDto2DungeonLocation(playerDto1), moveEnum)))
                .doOnNext(tupple2 -> Convert.changeLocation(tupple2.getT1(), tupple2.getT2()))
                .map(Tuple2::getT1)
                .flatMap(this.bookOfAdventureServiceClient::save)
                .map(it -> Map.of("result", true));
    }

    private boolean isMoved(PlayerDto playerDto) {
        return true;
    }

    public Mono<Map<String, Object>> pickup(String userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .flatMap(playerDto -> Mono.zip(Mono.just(playerDto),
                        this.dungeonServiceClient.whatIsOnMyFeet(Convert.playDto2DungeonLocation(playerDto))))
                .flatMap(tuple -> switch (tuple.getT2()) {
                    case None -> Mono.just(Map.of("result", false, "message", "NoObjectOnTheFloor"));
                    case Gold -> this.pickupGold(tuple.getT1());
                    case Object -> this.pickupObject(tuple.getT1());
                });
    }

    private Mono<Map<String, Object>> pickupGold(PlayerDto playerDto) {
        return this.dungeonServiceClient.pickUpGold(Convert.playDto2DungeonLocation(playerDto))
                .doOnNext(gold -> playerDto.setGold(playerDto.getGold() + gold.gold()))
                .doOnNext(gold -> this.bookOfAdventureServiceClient.save(playerDto))
                .map(gold -> Map.<String, Object>of("result", true, "type", 1, "gold", gold.gold()))
                .onErrorResume(it -> Mono.just(Map.of("result", false, "message", "NoObjectOnTheFloor")));
    }


    private Mono<Map<String, Object>> pickupObject(PlayerDto playerDto) {
        return this.bookOfAdventureServiceClient.getItemList(playerDto.getId())
                .collectList()
                .flatMap(this.objectServiceClient::getObjects)
                .map(thingMap -> {
                    Bag bag = new Bag();
                    bag.setContents(new ArrayList<>(thingMap.values()));
                    return bag;
                })
                .flatMap(bag -> Mono.zip(Mono.just(bag),
                        this.dungeonServiceClient.pickUpObject(Convert.playDto2DungeonLocation(playerDto))))
                .flatMap(tuple2 -> pickUpObjectExecute(playerDto, tuple2));
    }

    private Mono<Map<String, Object>> pickUpObjectExecute(PlayerDto playerDto, Tuple2<Bag, String> t) {
        if (t.getT1().getEmptySize() <= 0) {
            return Mono.just(Map.of("result", false, "message", "BagNoEmpty"));
        } else if (t.getT2().isEmpty()) {
            return Mono.just(Map.of("result", false, "message", "NoObjectOnTheFloor"));
        } else {
            t.getT1().getThingIdList().add(t.getT2());
            return this.bookOfAdventureServiceClient.changeObject(playerDto.getId(), t.getT1().getThingIdList())
                    .flatMap(it -> this.objectServiceClient.addHistory(t.getT2(), this.createMessageForPickUp(playerDto)))
                    .flatMap(it -> this.objectServiceClient.getObjectInfo(it.instanceId()))
                    .map(thingInfo -> Map.of("result", true, "type", 2, "itemName", thingInfo.name()));
        }
    }

    private String createMessageForPickUp(PlayerDto playerDto) {
        return "player: " + playerDto.getName() + "が取得";
    }

    public Mono<Map<String, Boolean>> downStairs(String userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .flatMap(playerDto -> Mono.zip(Mono.just(playerDto),
                        this.dungeonServiceClient.downStairs(Convert.playDto2DungeonLocation(playerDto))))
                .map(tuple -> {
                    tuple.getT1().setLocation(Convert.dungeonLocation2PlayerLocation(tuple.getT2()));
                    return tuple.getT1();
                })
                .flatMap(this.bookOfAdventureServiceClient::save)
                .map(it -> Map.of("result", true));
    }

    public Mono<Map<String, Boolean>> upStairs(String userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId)
                .flatMap(playerDto -> Mono.zip(Mono.just(playerDto),
                        this.dungeonServiceClient.upStairs(Convert.playDto2DungeonLocation(playerDto))))
                .map(tuple -> {
                    tuple.getT1().setLocation(Convert.dungeonLocation2PlayerLocation(tuple.getT2()));
                    return tuple.getT1();
                })
                .flatMap(this.bookOfAdventureServiceClient::save)
                .map(it -> Map.of("result", true));
    }

    public Mono<Boolean> existPlayer(String userName) {
        return this.bookOfAdventureServiceClient.exist(userName);
    }

    public Mono<String> create(String userName) {
        ReproducibleRandom r = new ReproducibleRandom(0);
        Map<String, Object> playerInfo =
                Map.of("hp", r.next(), "stamina", r.next(), "actionInterval", r.next(), "seed", r.next());
        return this.bookOfAdventureServiceClient.create(userName, playerInfo);
    }

    public Mono<PlayerDto> getPlayerInfo(String userId) {
        return this.bookOfAdventureServiceClient.getPlayer(userId);
    }
}
