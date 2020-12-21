package net.hero.rogueb.objectclient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "net.hero.rogueb.object.server")
@EnableConfigurationProperties({ObjectProperty.class})
public class ObjectServiceConfiguration {
    private final ObjectProperty property;

    public ObjectServiceConfiguration(ObjectProperty property){
        this.property = property;
    }

    @Bean
    public ObjectServiceClient objectServiceClient(){
        return new ObjectServiceClient(this.property.getServer());
    }
}
