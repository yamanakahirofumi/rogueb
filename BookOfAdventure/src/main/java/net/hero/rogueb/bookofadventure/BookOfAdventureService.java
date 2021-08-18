package net.hero.rogueb.bookofadventure;

import net.hero.rogueb.bookofadventure.domain.PlayerDomain;
import net.hero.rogueb.bookofadventure.domain.PlayerObjectDomain;
import net.hero.rogueb.bookofadventure.repository.PlayerObjectRepository;
import net.hero.rogueb.bookofadventure.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Transactional
@Service
public class BookOfAdventureService {
    private final PlayerRepository playerRepository;
    private final PlayerObjectRepository playerObjectRepository;

    public BookOfAdventureService(PlayerRepository playerRepository,
                                  PlayerObjectRepository playerObjectRepository) {
        this.playerRepository = playerRepository;
        this.playerObjectRepository = playerObjectRepository;
    }

    public Mono<Boolean> exist(String userName) {
        return this.playerRepository.existsByName(userName);
    }

    public Mono<PlayerDomain> getPlayerById(String id) {
        return this.playerRepository.findById(id);
    }

    public Flux<PlayerDomain> getPlayer(String userName) {
        return this.playerRepository.findByName(userName);
    }

    public Mono<String> save(PlayerDomain playerDomain) {
        return this.playerRepository.save(playerDomain)
                .map(PlayerDomain::getId);
    }

    public Mono<String> create(String userName, Map<String, Object> currentStatus) {
        return this.playerRepository.save(this.createPlayerDomain(userName, "localhost", currentStatus))
                .map(PlayerDomain::getId);
    }

    private PlayerDomain createPlayerDomain(String userName, String nameSpace, Map<String, Object> currentStatus) {
        PlayerDomain playerDomain = new PlayerDomain();
        playerDomain.setName(userName);
        playerDomain.setNamespace(nameSpace);
        playerDomain.setCurrentStatus(currentStatus);
        return playerDomain;
    }

    public Flux<Integer> getItemList(String playerId) {
        return this.playerObjectRepository.findByPlayerId(playerId)
                .flatMapIterable(PlayerObjectDomain::getObjectIdList);
    }

    public Mono<String> changeObject(String playerId, List<Integer> objectIdList) {
        return this.playerObjectRepository.findByPlayerId(playerId)
                .take(1)
                .doOnNext(it -> it.setObjectIdList(objectIdList))
                .flatMap(this.playerObjectRepository::save)
                .map(PlayerObjectDomain::getId)
                .elementAt(0);
    }
}
