package net.hero.rogueb.bookofadventureclient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value ="net.hero.rogueb.bookofadventure.server")
@EnableConfigurationProperties({BookOfAdventureProperty.class})
public class BookOfAdventureServiceClientConfiguration {
    private final BookOfAdventureProperty property;

    public BookOfAdventureServiceClientConfiguration(BookOfAdventureProperty property){
        this.property = property;
    }
    @Bean
    public BookOfAdventureServiceClient bookOfAdventureServiceClient(){
        return new BookOfAdventureServiceClient(this.property.getServer());
    }
}
