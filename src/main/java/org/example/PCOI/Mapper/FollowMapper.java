package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface FollowMapper {
    @Insert("INSERT INTO follow (followerId, followedId) VALUES (#{followerId}, #{followeeId})")
    void followUser(String followerId, String followeeId);

    @Delete("DELETE FROM follow WHERE followerId = #{followerId} AND followedId = #{followeeId}")
    void unfollowUser(String followerId, String followeeId);

    @Select("SELECT 1 FROM follow WHERE followerId = #{followerId} AND followedId = #{followeeId} LIMIT 1")
    boolean isFollow(String followerId, String followeeId);
}
