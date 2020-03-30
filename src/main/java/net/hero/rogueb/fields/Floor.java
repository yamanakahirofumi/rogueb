package net.hero.rogueb.fields;

import net.hero.rogueb.character.Player;
import net.hero.rogueb.math.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Floor {

    /**
     * 階層
     */
    private final int level;
    private final Coordinate maxSize;
    private List<Room> rooms;
    private List<List<String>> fields;
    private List<Player> playerList;

    public Floor(int level, Player player) {
        this.level = level;
        this.maxSize = new Coordinate2D(80, 80);
        this.rooms = new ArrayList<>();
        this.playerList = new ArrayList<>();
        player.getLocation().floor = this;
        player.getLocation().position = new Coordinate2D(1 + Random.rnd(77), 1+ Random.rnd(77));
        this.playerList.add(player);
        Room room = new Room(new Coordinate2D(1, 1), new Coordinate2D(78, 78));
        this.rooms.add(room);
        this.fields = Stream.generate(() ->
                Stream.generate(() -> "#").limit(((Coordinate2D) this.maxSize).getX()).collect(Collectors.toList())
        ).limit(((Coordinate2D) this.maxSize).getY()).collect(Collectors.toList());
    }

    private void changeDisplay(){
        for (Room r : this.rooms) {
            var display = r.getDisplay();
            for (var i = 0; i < ((Coordinate2D) r.getSize()).getX(); i++) {
                var xLine = this.fields.get(((Coordinate2D) r.getPosition()).getX() +i);
                var dxLine = display.get(i);
                for (var j = 0; j < ((Coordinate2D) r.getSize()).getY(); j++) {
                    xLine.set(((Coordinate2D) r.getPosition()).getY()+j, dxLine.get(j));
                }
            }
        }
        for(Player p: this.playerList){
            Coordinate2D position = (Coordinate2D)p.getLocation().position;
            this.fields.get(position.getX()).set(position.getY(), "@");
        }
    }

    public List<List<String>> getFields() {
        this.changeDisplay();
        return fields;
    }
}
