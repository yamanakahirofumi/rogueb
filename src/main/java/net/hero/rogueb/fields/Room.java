package net.hero.rogueb.fields;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO:recordにintellijのメジャーで対応したら
class Room{
    private final Coordinate position;
    private final Coordinate size;

    Room(Coordinate postion, Coordinate size){
        this.position = postion;
        this.size = size;
    }

    public List<List<String>> getDisplay(){
        return Stream.generate(() ->
                Stream.generate(() -> ".").limit(((Coordinate2D)this.size).getX()).collect(Collectors.toList())
        ).limit(((Coordinate2D)this.size).getY()).collect(Collectors.toList());
    }

    public Coordinate getSize(){
        return this.size;
    }

    public Coordinate getPosition() {
        return position;
    }
}
