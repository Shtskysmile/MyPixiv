package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.PCOI.Entity.User;

import java.util.List;
@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user (username, password, avatar, sex) VALUES (#{username},#{password},#{avatar},#{sex})")
    void insertUser(User user);

    @Update("UPDATE user SET username=#{username}, password=#{password}, avatar=#{avatar},sex =#{sex} WHERE userId=#{userId}")
    void updateUser(User user);

    @Select("SELECT * FROM user WHERE userId = #{userId}")
    User selectUserById(String user);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectUserByName(String username);

    @Select("""
            SELECT u.*
            FROM user u
            JOIN follow f ON u.userId = f.followerId
            WHERE f.followedId = #{userId}
         """)
    List<User> selectFollowerUsersByUserId(String userId);
    @Select("""
            SELECT u.*
            FROM user u
            JOIN follow f ON u.userId = f.followedId
            WHERE f.followerId = #{userId}
    """)
    List<User> selectFollowedUsersByUserId(String userId);
}
