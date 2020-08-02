package net.hero.rogueb.bag;

import net.hero.rogueb.objects.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Bag {
    private List<Thing> contents;
    private final int limitSize;
    private int size;

    public Bag() {
        this.contents = new ArrayList<>();
        this.limitSize = 23;
        this.size = 0;
    }

    public boolean addContents(Thing thing) {
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
        return this.contents.stream().map(Thing::getId).collect(Collectors.toList());
    }

    public void setContents(List<Thing> contents) {
        this.contents = contents;
    }
}
