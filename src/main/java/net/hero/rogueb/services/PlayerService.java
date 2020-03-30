package net.hero.rogueb.services;

import net.hero.rogueb.character.Human;
import net.hero.rogueb.character.Player;
import net.hero.rogueb.fields.Dungeon;
import net.hero.rogueb.fields.MoveEnum;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlayerService {

    public Map<String, String> create(String userName) {
        Player player = Human.getInstance(userName);
        Dungeon.getInstance().setPlayer(player);
        return Map.of("key", "Hello World");
    }

    public Map<String, Boolean> top(String userName){
        return Map.of("result", Human.getInstance(userName).move(MoveEnum.Top));
    }

    public Map<String,Boolean> down(String userName){
        return Map.of("result", Human.getInstance(userName).move(MoveEnum.Down));
    }

    public Map<String, Boolean> right(String userName){
        return Map.of("result", Human.getInstance(userName).move(MoveEnum.Right));
    }

    public Map<String,Boolean> left(String userName){
        return Map.of("result", Human.getInstance(userName).move(MoveEnum.Left));
    }
}
