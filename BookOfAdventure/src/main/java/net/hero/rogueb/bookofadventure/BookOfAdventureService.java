package net.hero.rogueb.bookofadventure;

import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.bookofadventure.dto.PlayerObjectDto;
import net.hero.rogueb.bookofadventure.mapper.CharacterMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class BookOfAdventureService {
    private final CharacterMapper characterMapper;

    public BookOfAdventureService(CharacterMapper characterMapper) {
        this.characterMapper = characterMapper;
    }

    public boolean exist(String userName) {
        return this.characterMapper.countByName(userName) > 0;
    }

    public PlayerDto getPlayer(int id) {
        return this.characterMapper.findById(id);
    }

    public PlayerDto getPlayer(String userName) {
        return this.characterMapper.findByName(userName);
    }

    public void save(PlayerDto playerDto) {
        this.characterMapper.update(playerDto);
        this.characterMapper.deleteLocation(playerDto);
        this.characterMapper.insertLocation(playerDto.getId(), playerDto.getLocationDto());
    }

    public int create(String userName) {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName(userName);
        playerDto.setNamespace("localhost");
        this.characterMapper.insert(playerDto);
        return playerDto.getId();
    }

    public List<Integer> getItemList(int playerId) {
        return this.characterMapper.getObjectByPlayerId(playerId);
    }

    public void changeObject(int playerId, List<Integer> objectIdList) {
        this.characterMapper.deleteObject(playerId);
        this.characterMapper.insertObject(objectIdList.stream()
                .map(it -> new PlayerObjectDto(playerId, it))
                .collect(Collectors.toList()));
    }
}
