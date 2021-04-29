package net.hero.rogueb.dungeonclient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "net.hero.rogueb.dungeon.server")
@EnableConfigurationProperties({DungeonProperty.class})
public class DungeonServiceConfiguration {
    private final DungeonProperty property;

    public DungeonServiceConfiguration(DungeonProperty property) {
        this.property = property;
    }

    @Bean
    public DungeonServiceClient dungeonServiceClient() {
        return new DungeonServiceClient(this.property.getServer());
    }
}
