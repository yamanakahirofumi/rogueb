package net.hero.rogueb.controllers;

import net.hero.rogueb.services.FieldsService;
import net.hero.rogueb.services.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/player/{userId}")
    public Map<String, String> gotoDungeon(@PathVariable("userId") int userId) {
        return this.playerService.gotoDungeon(userId);
    }

    @PutMapping("/player/{userId}/command/top")
    public Map<String, Boolean> top(@PathVariable("userId") int userId) {
        return this.playerService.top(userId);
    }

    @PutMapping("/player/{userId}/command/down")
    public Map<String, Boolean> down(@PathVariable("userId") int userId) {
        return this.playerService.down(userId);
    }

    @PutMapping("/player/{userId}/command/right")
    public Map<String, Boolean> right(@PathVariable("userId") int userId) {
        return this.playerService.right(userId);
    }

    @PutMapping("/player/{userId}/command/left")
    public Map<String, Boolean> left(@PathVariable("userId") int userId) {
        return this.playerService.left(userId);
    }

    @PutMapping("/player/{userId}/command/pickup")
    public Map<String, Object> pickUp(@PathVariable("userId") int userId) {
        return this.playerService.pickup(userId);
    }

    @PutMapping("/player/{userId}/command/downStairs")
    public Map<String, Boolean> downStairs(@PathVariable("userId") int userId) {
        return this.playerService.downStairs(userId);
    }

    @PutMapping("/player/{userId}/command/upStairs")
    public Map<String, Boolean> upStairs(@PathVariable("userId") int userId) {
        return this.playerService.upStairs(userId);
    }

    @GetMapping("/fields/{userId}")
    public List<List<String>> getFields(@PathVariable("userId") int userId) {
        return this.fieldsService.getFields(userId);
    }

    @GetMapping("fields/{userId}/info")
    public Map<String, String> getDungeonInfo(@PathVariable("userId") int userId) {
        return this.fieldsService.getDungeonInfo(userId);
    }
}
