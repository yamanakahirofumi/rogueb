package net.hero.rogueb.object;

import org.springframework.stereotype.Service;

@Service
public class ObjectService {

    public Ring createRing(){
        return new Ring("protection");
    }
}
