package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface FollowMapper {
    @Insert("INSERT INTO follow (followerId, followedId) VALUES (#{followerId}, #{followedId})")
    void insertFollow(@Param("followerId") String followerId, @Param("followedId") String followedId); //followerId关注followedId 即follower是粉丝,followed是被关注者

    @Delete("DELETE FROM follow WHERE followerId = #{followerId} AND followedId = #{followedId}") //followerId关注followedId 即follower是粉丝,followed是被关注者
    void deleteFollow(@Param("followerId") String followerId, @Param("followedId") String followedId);

    @Select("SELECT 1 FROM follow WHERE followerId = #{followerId} AND followedId = #{followedId} LIMIT 1") //followerId关注followedId 即follower是粉丝,followed是被关注者
    boolean isFollow(@Param("followerId") String followerId, @Param("followedId") String followedId);
}
