package net.hero.rogueb.objects.mapper;

import net.hero.rogueb.objects.Ring;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ObjectMapper {

    List<Ring> findRing();

    Ring findById(int id);
}
