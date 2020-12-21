package net.hero.rogueb.bag;


import net.hero.rogueb.objectclient.o.ThingSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Bag {
    private List<ThingSimple> contents;
    private final int limitSize;
    private int size;

    public Bag() {
        this.contents = new ArrayList<>();
        this.limitSize = 23;
        this.size = 0;
    }

    public boolean addContents(ThingSimple thing) {
        // TODO:はじめは個数で制限、ある程度のところで重さを定義
        var result = this.size < this.limitSize;
        if (result) {
            this.contents.add(thing);
            this.size++;
        }
        return result;
    }

    public int getEmptySize(){
        return this.limitSize - this.size;
    }

    public List<Integer> getThingIdList(){
        return this.contents.stream().map(ThingSimple::id).collect(Collectors.toList());
    }

    public void setContents(List<ThingSimple> contents) {
        this.contents = contents;
    }
}
