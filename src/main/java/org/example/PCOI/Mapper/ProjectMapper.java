package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * ProjectMapper
 * 项目表的数据库操作接口
 */
@Mapper
public interface ProjectMapper {
    /**
     * 插入新项目
     * @param project 项目对象
     * @return void
     */
    @Insert("INSERT INTO project (name, type, status, description, sec_inc_rec, owner, start_date, end_date) VALUES (#{name}, #{type}, #{status}, #{description}, #{sec_inc_rec}, #{owner}, #{startDate}, #{endDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertProject(Project project);

    /**
     * 根据ID查询项目基本信息（用于实验室关联查询，避免循环引用）
     * @param id 项目ID
     * @return 项目基本信息
     */
    @Select("SELECT id, name, type, status, description, owner, start_date, end_date FROM project WHERE id = #{id}")
    Project selectProjectBasicById(Integer id);

    /**
     * 根据ID查询项目
     * @param id 项目ID
     * @return 项目对象
     */
    @Select("SELECT * FROM project WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "description", column = "description"),
        @Result(property = "sec_inc_rec", column = "sec_inc_rec"),
        @Result(property = "owner", column = "owner"),
        @Result(property = "startDate", column = "start_date"),
        @Result(property = "endDate", column = "end_date"),
        @Result(property = "users", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectUsersByProjectId"))
    })
    Project selectProjectById(Integer id);

    /**
     * 查询所有项目
     * @return 项目列表
     */
    @Select("SELECT * FROM project")
    List<Project> selectAllProjects();

    /**
     * 更新项目信息
     * @param project 项目对象
     * @return void
     */
    @Update("UPDATE project SET name = #{name}, type = #{type}, status = #{status}, description = #{description}, sec_inc_rec = #{sec_inc_rec}, owner = #{owner}, start_date = #{startDate}, end_date = #{endDate} WHERE id = #{id}")
    void updateProject(Project project);

    /**
     * 根据ID删除项目
     * @param id 项目ID
     * @return void
     */
    @Delete("DELETE FROM project WHERE id = #{id}")
    void deleteProject(Integer id);
} 