package net.hero.rogueb.object.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CreatedObjectMapper {
    int countById(int id);
    void updateCount(int id);
    void insertCount(int id);
}
