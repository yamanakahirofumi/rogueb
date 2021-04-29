package net.hero.rogueb.bookofadventure.mapper;

import net.hero.rogueb.bookofadventure.dto.LocationDto;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.bookofadventure.dto.PlayerObjectDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CharacterMapper {

    void insert(PlayerDto playerDto);

    void update(PlayerDto playerDto);

    PlayerDto findById(int id);

    PlayerDto findByName(String name);

    int countByName(String name);

    void deleteLocation(PlayerDto playerDto);

    void insertLocation(int id, LocationDto locationDto);

    List<Integer> getObjectByPlayerId(int id);

    void deleteObject(int id);

    void insertObject(List<PlayerObjectDto> playerObjectDtoList);
}
