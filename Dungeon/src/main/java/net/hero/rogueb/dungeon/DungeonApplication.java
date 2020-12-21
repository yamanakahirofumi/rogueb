package net.hero.rogueb.dungeon;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@SpringBootApplication
public class DungeonApplication {
    public static void main(String[] args) {
        SpringApplication.run(DungeonApplication.class, args);
    }

    /** TODO:jacksonがrecord対応するまでのワークアラウンド **/
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(){
        JacksonAnnotationIntrospector implicitRecordAI = new JacksonAnnotationIntrospector() {

            @Override
            public PropertyName findNameForDeserialization(Annotated a) {
                PropertyName nameForDeserialization = super.findNameForDeserialization(a);
                // when @JsonDeserialize is used, USE_DEFAULT is default
                // preventing the implicit constructor to be found
                if (PropertyName.USE_DEFAULT.equals(nameForDeserialization)
                        && a instanceof AnnotatedParameter
                        && ((AnnotatedParameter) a).getDeclaringClass().isRecord()) {
                    String str = findImplicitPropertyName((AnnotatedParameter) a);
                    if (str != null && !str.isEmpty()) {
                        return PropertyName.construct(str);
                    }
                }
                return nameForDeserialization;
            }

            @Override
            public String findImplicitPropertyName(AnnotatedMember m) {
                if (m.getDeclaringClass().isRecord()
                        && m instanceof AnnotatedParameter parameter) {
                    return m.getDeclaringClass().getRecordComponents()[parameter.getIndex()].getName();
                }
                return super.findImplicitPropertyName(m);
            }
        };
        return new Jackson2ObjectMapperBuilderCustomizer(){

            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
                jacksonObjectMapperBuilder.annotationIntrospector(implicitRecordAI);
            }
        };
    }
}
