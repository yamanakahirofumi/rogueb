package net.hero.rogueb.bookofadventureclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "net.hero.rogueb.bookofadventure")
public class BookOfAdventureProperty {
    private String server;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
