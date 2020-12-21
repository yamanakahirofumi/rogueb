package net.hero.rogueb.dungeon.mapper;

import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.dungeon.dto.DungeonPlayerDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface DungeonMapper {

    DungeonDto findByName(String name);

    DungeonDto findById(int id);

    DungeonDto findByIdAndPlayerId(int id, int playerId);

    int insert(DungeonDto dungeonDto);

    void deleteDungeonPlayer(DungeonPlayerDto dungeonPlayerDto);

    void insertDungeonPlayer(DungeonPlayerDto dungeonPlayerDto);
}
