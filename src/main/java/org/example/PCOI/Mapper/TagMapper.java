package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.PCOI.Entity.Tag;

import java.util.List;
@Mapper
public interface TagMapper {
    @Insert("INSERT INTO tag (tagName) VALUES (#{tagName})")
    void insertTag(String tagName);

    @Delete("DELETE FROM tag WHERE tagName = #{tagName}")
    void deleteTag(String tagName);

    @Select("SELECT 1 FROM tag WHERE tagName = #{tagName} LIMIT 1")
    boolean isTagExist(String tagName);

    @Select("SELECT * FROM tag WHERE tagName = #{tagName}")
    Tag selectTagByName(String tagName);

    @Select("SELECT * FROM tag")
    List<Tag> selectAllTags();
}
