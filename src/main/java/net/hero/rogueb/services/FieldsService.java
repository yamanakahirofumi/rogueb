package net.hero.rogueb.services;

import net.hero.rogueb.bookofadventure.BookOfAdventureService;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.DungeonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FieldsService {
    private final BookOfAdventureService bookOfAdventureService;
    private final DungeonService dungeonService;

    public FieldsService(BookOfAdventureService bookOfAdventureService, DungeonService dungeonService) {
        this.bookOfAdventureService = bookOfAdventureService;
        this.dungeonService = dungeonService;
    }

    public List<List<String>> getFields(int userId) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userId);
        return this.dungeonService.displayData(playerDto);
    }

    public Map<String, String> getDungeonInfo(int userId) {
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer(userId);
        return Map.of("level", String.valueOf(playerDto.getLocationDto().getLevel()),
                "name", this.dungeonService.getDungeonName(playerDto));
    }
}
