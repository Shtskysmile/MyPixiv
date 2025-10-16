package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.Contribution;
import java.util.List;

/**
 * UserMapper
 * 用户表的数据库操作接口
 */
@Mapper
public interface UserMapper {
    /**
     * 插入新用户
     * @param user 用户对象
     * @return void
     */
    @Insert("INSERT INTO user (id, username, password, identity, authority) VALUES (#{id}, #{username}, #{password}, #{identity}, #{authority})")
    void insertUser(User user);

    /**
     * 根据用户名查询用户，并自动填充用户的所有复杂成员
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "identity", column = "identity"),
        @Result(property = "authority", column = "authority"),
        @Result(property = "projectList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectProjectsByUserId")),
       /* @Result(property = "laboratorysList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectLaboratorysByUserId")),*/
        @Result(property = "equipmentList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectEquipmentByUserId")),
        @Result(property = "suppliesList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectSuppliesByUserId"))
    })
    User selectUserByName(String username);

    /**
     * 查询所有用户，并自动填充所有复杂成员
     * @return 用户列表
     */
    @Select("SELECT * FROM user")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "identity", column = "identity"),
        @Result(property = "authority", column = "authority"),
        @Result(property = "projectList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectProjectsByUserId")),
        /*@Result(property = "laboratorysList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectLaboratorysByUserId")),*/
        @Result(property = "equipmentList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectEquipmentByUserId")),
        @Result(property = "suppliesList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectSuppliesByUserId"))
    })
    List<User> selectAllUsers();

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return void
     */
    @Update("UPDATE user SET username = #{username}, password = #{password}, identity = #{identity}, authority = #{authority} WHERE id = #{id}")
    void updateUser(User user);

    /**
     * 根据用户名删除用户
     * @param username 用户名
     * @return void
     */
    @Delete("DELETE FROM user WHERE username = #{username}")
    void deleteUser(String username);

    /**
     * 查询某项目下的所有用户，并自动填充所有复杂成员
     * @param projectId 项目ID
     * @return 用户列表
     */
    @Select("SELECT u.* FROM user u JOIN user_project up ON u.id = up.user_id WHERE up.project_id = #{projectId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "identity", column = "identity"),
            @Result(property = "authority", column = "authority"),
            @Result(property = "projectList", column = "id",
                    many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectProjectsByUserId")),
//            @Result(property = "laboratorysList", column = "id",
//                    many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectLaboratorysByUserId")),
            @Result(property = "equipmentList", column = "id",
                    many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectEquipmentByUserId")),
            @Result(property = "suppliesList", column = "id",
                    many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectSuppliesByUserId"))
    })
    List<User> selectUsersByProjectId(Integer projectId);

    /**
     * 根据用户ID查询该用户参与的所有项目
     * @param userId 用户ID
     * @return 项目列表
     */
    @Select("SELECT p.* FROM project p JOIN user_project up ON p.id = up.project_id WHERE up.user_id = #{userId}")
    List<Project> selectProjectsByUserId(Integer userId);

    /**
     * 根据用户ID查询该用户所在的所有实验室
     * @param userId 用户ID
     * @return 实验室列表
     */
/*    @Select("SELECT l.* FROM laboratorys l JOIN user_laboratorys ul ON l.id = ul.laboratorys_id WHERE ul.user_id = #{userId}")
    List<Tag> selectLaboratorysByUserId(Integer userId);*/

    /**
     * 根据用户ID查询该用户租用的所有设备
     * @param userId 用户ID
     * @return 设备列表
     */
    @Select("SELECT e.* FROM equipment e WHERE e.renter_id = #{userId}")
    List<SecurityIssue> selectEquipmentByUserId(Integer userId);

    /**
     * 根据用户ID查询该用户租用的所有物资
     * @param userId 用户ID
     * @return 物资列表
     */
    @Select("SELECT s.* FROM supplies s WHERE s.renter_id = #{userId}")
    List<Contribution> selectSuppliesByUserId(Integer userId);

    /**
     * 根据用户ID查询用户基本信息（用于设备关联查询，避免循环引用）
     * @param id 用户ID
     * @return 用户基本信息
     */
    @Select("SELECT id, username, identity, authority FROM user WHERE id = #{id}")
    User selectUserBasicById(Integer id);

    /**
     * 根据用户ID查询用户，并自动填充所有复杂成员
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "identity", column = "identity"),
        @Result(property = "authority", column = "authority"),
        @Result(property = "projectList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectProjectsByUserId")),
//        @Result(property = "laboratorysList", column = "id",
//            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectLaboratorysByUserId")),
        @Result(property = "equipmentList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectEquipmentByUserId")),
        @Result(property = "suppliesList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.UserMapper.selectSuppliesByUserId"))
    })
    User selectUserByIdWithAllRelations(Integer id);
} 