package net.hero.rogueb.world;

import net.hero.rogueb.bookofadventure.BookOfAdventureService;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.dungeon.DungeonService;
import org.springframework.stereotype.Service;

@Service
public class WorldService {
    private final BookOfAdventureService bookOfAdventureService;
    private final DungeonService dungeonService;

    public WorldService(BookOfAdventureService bookOfAdventureService, DungeonService dungeonService) {
        this.bookOfAdventureService = bookOfAdventureService;
        this.dungeonService = dungeonService;
    }

    public PlayerDto bornPlayer(String playerName) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(playerName);
        if (playerDto != null) {
            throw new RuntimeException();
        }
        this.bookOfAdventureService.create(playerName);
        playerDto = this.bookOfAdventureService.getPlayer(playerName);
        return playerDto;
    }


    public DungeonDto getStartDungeon() {
        final String dungeonName = "dungeon";
        DungeonDto dungeonDto = this.dungeonService.findByName(dungeonName);
        if (dungeonDto == null) {
            int id = this.dungeonService.save(dungeonName);
            dungeonDto = new DungeonDto();
            dungeonDto.setId(id);
            dungeonDto.setName(dungeonName);
            return dungeonDto;
        } else {
            return dungeonDto;
        }
    }
}
