package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagRelationMapper {
    @Insert("INSERT INTO tag_relation (contributionId, tagId) VALUES (#{contributionId}, #{tagId})")
    void insertTagRelation(String contributionId, int tagId);
}
