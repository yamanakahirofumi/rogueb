package net.hero.rogueb.fields;

import net.hero.rogueb.character.Player;
import net.hero.rogueb.math.Random;
import net.hero.rogueb.object.Ring;
import net.hero.rogueb.object.Thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<Coordinate,Thing> things;

    public Floor(int level, Player player) {
        this.level = level;
        this.maxSize = new Coordinate2D(80, 80);
        this.rooms = new ArrayList<>();
        this.playerList = new ArrayList<>();
        this.things = new HashMap<>();
        player.getLocation().floor = this;
        player.getLocation().position = new Coordinate2D(1 + Random.rnd(77), 1+ Random.rnd(77));
        this.playerList.add(player);
        Room room = new Room(new Coordinate2D(1, 1), new Coordinate2D(78, 78));
        this.rooms.add(room);
        this.fields = Stream.generate(() ->
                Stream.generate(() -> "#").limit(((Coordinate2D) this.maxSize).getX()).collect(Collectors.toList())
        ).limit(((Coordinate2D) this.maxSize).getY()).collect(Collectors.toList());
        Thing thing = new Ring();
        Coordinate thingCoordiname = new Coordinate2D(1+ Random.rnd(77), 1+ Random.rnd(77));
        things.put(thingCoordiname, thing);
    }

    private void changeDisplay(){
        for (var r : this.rooms) {
            var display = r.getDisplay();
            for (var i = 0; i < ((Coordinate2D) r.getSize()).getX(); i++) {
                var xLine = this.fields.get(((Coordinate2D) r.getPosition()).getX() +i);
                var dxLine = display.get(i);
                for (var j = 0; j < ((Coordinate2D) r.getSize()).getY(); j++) {
                    xLine.set(((Coordinate2D) r.getPosition()).getY()+j, dxLine.get(j));
                }
            }
        }
        for(var s : this.things.entrySet()){
            Coordinate2D coordinate = (Coordinate2D) s.getKey();
            this.fields.get(coordinate.getX()).set(coordinate.getY(), s.getValue().getDisplay());
        }
        for(var p: this.playerList){
            Coordinate2D position = (Coordinate2D)p.getLocation().position;
            this.fields.get(position.getX()).set(position.getY(), "@");
        }
    }

    public List<List<String>> getFields() {
        this.changeDisplay();
        return fields;
    }

    public boolean isObject(Coordinate position) {
        return this.things.containsKey(position);
    }

    public Thing getThings(Coordinate position) {
        return this.things.get(position);
    }

    public void removeThings(Coordinate position){
        this.things.remove(position);
    }
}
