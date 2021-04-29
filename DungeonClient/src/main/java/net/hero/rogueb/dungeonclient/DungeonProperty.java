package net.hero.rogueb.dungeonclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "net.hero.rogueb.dungeon")
public class DungeonProperty {
    private String server;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
