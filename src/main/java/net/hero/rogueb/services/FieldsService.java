package net.hero.rogueb.services;

import net.hero.rogueb.fields.Dungeon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldsService {

    public List<List<String>> getFields() {
        return Dungeon.getInstance().getFields();
    }

}
