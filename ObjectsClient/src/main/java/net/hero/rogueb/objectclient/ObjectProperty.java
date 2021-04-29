package net.hero.rogueb.objectclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "net.hero.rogueb.object")
public class ObjectProperty {
    private String server;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
