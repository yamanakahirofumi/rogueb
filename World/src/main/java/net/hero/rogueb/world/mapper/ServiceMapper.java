package net.hero.rogueb.world.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ServiceMapper {
    String findByType(int typeId);
}
