package net.hero.rogueb.dungeon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class DungeonApplication {
    public static void main(String[] args) {
        SpringApplication.run(DungeonApplication.class, args);
    }
}
