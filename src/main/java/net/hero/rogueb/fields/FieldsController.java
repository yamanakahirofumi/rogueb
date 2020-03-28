package net.hero.rogueb.fields;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/fields")
public class FieldsController {

    @GetMapping("/main")
    public List<List<String>> getFields() {
        List<List<String>> fields = Stream.generate(() -> Stream.generate(() -> "#").limit(80).collect(Collectors.toList())).limit(80).collect(Collectors.toList());
        return fields;
    }
}
