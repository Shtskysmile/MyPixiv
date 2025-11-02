package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface LikeMapper {
    @Insert("INSERT INTO likes (userId, contributionId) VALUES (#{userId}, #{contributionId})")
    void insertLike(@Param("userId") String userId, @Param("contributionId") String contributionId);

    @Delete("DELETE FROM likes WHERE userId = #{userId} AND contributionId = #{contributionId}")
    void deleteLike(@Param("userId") String userId, @Param("contributionId") String contributionId);

    @Select("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = #{userId} AND contributionId = #{contributionId})")
    boolean isLike(@Param("userId") String userId, @Param("contributionId") String contributionId);
}
