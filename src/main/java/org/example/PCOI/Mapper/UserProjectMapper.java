package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;

/**
 * UserProjectMapper
 * 用户-项目关联表的数据库操作接口（只做增删）
 */
@Mapper
public interface UserProjectMapper {
    /**
     * 插入一条用户-项目关联
     */
    @Insert("INSERT INTO user_project (user_id, project_id) VALUES (#{userId}, #{projectId})")
    void insertUserProject(@Param("userId") Integer userId, @Param("projectId") Integer projectId);

    /**
     * 删除一条用户-项目关联
     */
    @Delete("DELETE FROM user_project WHERE user_id = #{userId} AND project_id = #{projectId}")
    void deleteUserProject(@Param("userId") Integer userId, @Param("projectId") Integer projectId);
} 