package net.hero.rogueb.bookofadventure;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class BookOfAdventureController {
    private final BookOfAdventureService service;

    public BookOfAdventureController(BookOfAdventureService service){
        this.service = service;
    }

    @GetMapping("/name/{userName}/exist")
    public boolean exist(@PathVariable("userName") String userName){
        return this.service.exist(userName);
    }

    @PutMapping("{userName}")
    public void save(@PathVariable("userName") String userName){

    }
    @PostMapping("{userName}")
    public int create(@PathVariable("userName") String userName){
        return this.service.create(userName);
    }

}
