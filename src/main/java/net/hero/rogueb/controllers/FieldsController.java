package net.hero.rogueb.controllers;

import net.hero.rogueb.services.FieldsService;
import net.hero.rogueb.services.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FieldsController {

    private final FieldsService fieldsService;
    private final PlayerService playerService;

    FieldsController(FieldsService fieldsService, PlayerService playerService) {
        this.fieldsService = fieldsService;
        this.playerService = playerService;
    }

    @PostMapping("/player/{userName}")
    public Map<String, String> createUser(String userName) {
        return this.playerService.create(userName);
    }

    @PutMapping("/player/{userName}/command/top")
    public Map<String,Boolean> top(String userName){
        return this.playerService.top(userName);
    }

    @PutMapping("/player/{userName}/command/down")
    public Map<String,Boolean> down(String userName){
        return this.playerService.down(userName);
    }

    @PutMapping("/player/{userName}/command/right")
    public Map<String,Boolean> right(String userName){
        return this.playerService.right(userName);
    }

    @PutMapping("/player/{userName}/command/left")
    public Map<String,Boolean> left(String userName){
        return this.playerService.left(userName);
    }

    @GetMapping("/fields/main")
    public List<List<String>> getFields() {
        return this.fieldsService.getFields();
    }
}
