package net.hero.rogueb.bookofadventure.mapper;

import net.hero.rogueb.bookofadventure.dto.LocationDto;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CharacterMapper {

    void insert(PlayerDto playerDto);

    void update(PlayerDto playerDto);

    PlayerDto findById(int id);

    PlayerDto findByName(String name);

    void deleteLocation(PlayerDto playerDto);

    void insertLocation(int id, LocationDto locationDto);
}
