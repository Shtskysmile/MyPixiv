package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface LikeMapper {
    @Insert("INSERT INTO likes (userId, contributionId) VALUES (#{userId}, #{contributionId})")
    void insertLike(String userId, String contributionId);

    @Delete("DELETE FROM likes WHERE userId = #{userId} AND contributionId = #{contributionId}")
    void deleteLike(String userId, String contributionId);

    @Select("SELECT 1 FROM likes WHERE userId = #{userId} AND contributionId = #{contributionId} LIMIT 1")
    boolean isLike(String userId, String contributionId);
}
