package net.hero.rogueb.character;

import net.hero.rogueb.bag.Bag;
import net.hero.rogueb.fields.Coordinate;
import net.hero.rogueb.fields.Coordinate2D;
import net.hero.rogueb.fields.Location;
import net.hero.rogueb.fields.MoveEnum;

public class Human implements Player {
    private static Human instance;
    private String name;
    private Location location;
    private Bag bag;

    public static Human getInstance(String name) {
        if (instance == null) {
            instance = new Human(name);
        }
        return instance;
    }

    private Human(String name) {
        this.name = name;
        this.location = new Location();
        this.bag = new Bag();
    }

    public Human(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean move(MoveEnum moveEnum) {
        var floor = this.location.floor;
        Coordinate2D position = (Coordinate2D) this.location.position;
        Coordinate2D newPosition = new Coordinate2D(position.getX() + moveEnum.getX(), position.getY() + moveEnum.getY());
        boolean result = !floor.getFields().get(newPosition.getX()).get(newPosition.getY()).equals("#");
        if(result) {
            this.location.position = newPosition;
        }
        return result;
    }

    @Override
    public boolean pickUp() {
        Coordinate position = this.location.position;
        if(!this.location.floor.isObject(position)) {
            return false;
        }
        boolean result = this.bag.addContents(this.location.floor.getThings(position));
        if(result){
            this.location.floor.removeThings(position);
        }
        return result;
    }
}
