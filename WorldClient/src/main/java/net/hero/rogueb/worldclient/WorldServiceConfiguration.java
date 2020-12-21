package net.hero.rogueb.worldclient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "net.hero.rogueb.world.server")
@EnableConfigurationProperties({WorldProperty.class})
public class WorldServiceConfiguration {
    private final WorldProperty property;

    public WorldServiceConfiguration(WorldProperty property){
        this.property = property;
    }

    @Bean
    public WorldServiceClient worldServiceClient(){
        return new WorldServiceClient(this.property.getServer());
    }
}
