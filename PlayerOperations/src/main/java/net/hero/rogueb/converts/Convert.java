package net.hero.rogueb.converts;

import net.hero.rogueb.bookofadventureclient.o.PlayerDto;
import net.hero.rogueb.dungeonclient.o.Coordinate2D;
import net.hero.rogueb.dungeonclient.o.DungeonLocation;
import net.hero.rogueb.exceptions.IllegalValueException;

import java.util.Map;

public class Convert {
    public static DungeonLocation playDto2DungeonLocation(PlayerDto playerDto) {
        Map<String, Object> location = playerDto.getLocation();
        if (location.get("dungeonId") instanceof String dungeonId && location.get("level") instanceof Integer level &&
                location.get("x") instanceof Integer x && location.get("y") instanceof Integer y) {
            return new DungeonLocation(dungeonId, playerDto.getId(), level, new Coordinate2D(x, y));
        } else {
            throw new IllegalValueException("This is maybe an unreachable line.");
        }
    }

    public static void changeLocation(PlayerDto playerDto, Coordinate2D coordinate2D) {
        Map<String, Object> location = playerDto.getLocation();
        if (location.get("dungeonId") instanceof String dungeonId && location.get("level") instanceof Integer level) {
            playerDto.setLocation(Map.of("dungeonId", dungeonId, "level", level,
                    "x", coordinate2D.x(), "y", coordinate2D.y()));
        } else {
            throw new IllegalValueException("This is maybe an unreachable line.");
        }
    }

    public static Map<String, Object> dungeonLocation2PlayerLocation(DungeonLocation dungeonLocation) {
        return Map.of("dungeonId", dungeonLocation.dungeonId(), "level", dungeonLocation.level(),
                "x", dungeonLocation.coordinate2D().x(), "y", dungeonLocation.coordinate2D().y());
    }
}
