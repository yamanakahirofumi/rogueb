package net.hero.rogueb.fields;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO:recordにintellijのメジャーで対応したら
class Room{
    private final Coordinate2D position;
    private final Coordinate2D size;

    Room(Coordinate2D position, Coordinate2D size){
        this.position = position;
        this.size = size;
    }

    public List<List<String>> getDisplay(){
        return Stream.generate(() ->
                Stream.generate(() -> ".").limit(this.size.getX()).collect(Collectors.toList())
        ).limit(this.size.getY()).collect(Collectors.toList());
    }

    public Coordinate getSize(){
        return this.size;
    }

    public Coordinate getPosition() {
        return position;
    }
}
