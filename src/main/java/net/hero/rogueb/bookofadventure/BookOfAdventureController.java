package net.hero.rogueb.bookofadventure;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class BookOfAdventureController {
    private final BookOfAdventureService service;

    public BookOfAdventureController(BookOfAdventureService service){
        this.service = service;
    }

    @GetMapping("/user/{userName}/exist")
    public boolean exist(String userName){
        return this.service.exist(userName);
    }

    @PostMapping("/user/{userName}")
    public void save(String userName){

    }

}
