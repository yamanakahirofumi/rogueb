package net.hero.rogueb.services;

import net.hero.rogueb.bookofadventure.BookOfAdventureService;
import net.hero.rogueb.bookofadventure.dto.PlayerDto;
import net.hero.rogueb.dungeon.dto.DungeonDto;
import net.hero.rogueb.dungeon.DungeonService;
import net.hero.rogueb.fields.Dungeon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldsService {
    private final BookOfAdventureService bookOfAdventureService;
    private final DungeonService dungeonService;

    public FieldsService(BookOfAdventureService bookOfAdventureService, DungeonService dungeonService){
        this.bookOfAdventureService = bookOfAdventureService;
        this.dungeonService = dungeonService;
    }

    public List<List<String>> getFields() {
        // TODO
        PlayerDto playerDto = this.bookOfAdventureService.getPlayer("hero");
        return this.dungeonService.displayData(playerDto);
    }

}
