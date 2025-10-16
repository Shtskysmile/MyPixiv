package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.PCOI.Entity.SecurityIssue;

import java.util.List;

/**
 * EquipmentMapper
 * 设备表的数据库操作接口
 */
@Mapper
public interface EquipmentMapper {
    /**
     * 插入新设备
     * @param securityIssue 设备对象
     * @return void
     */
    @Insert("INSERT INTO equipment (id, name, status, laboratory_id, renter_id, description) VALUES (#{id}, #{name}, #{status}, #{owner.id}, #{renter.id}, #{description})")
    void insertEquipment(SecurityIssue securityIssue);

    /**
     * 根据ID查询设备，并自动填充实验室和租用者及其复杂成员
     * @param id 设备ID
     * @return 设备对象
     */
    @Select("SELECT * FROM equipment WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "status", column = "status"),
        @Result(property = "owner", column = "laboratory_id",
            one = @One(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectLaboratoryById")),
        @Result(property = "renter", column = "renter_id",
            one = @One(select = "org.example.laboratory.Mapper.UserMapper.selectUserByIdWithAllRelations")),
        @Result(property = "description", column = "description")
    })
    SecurityIssue selectEquipmentById(Integer id);

    /**
     * 查询所有设备，并自动填充实验室和租用者及其复杂成员
     * @return 设备列表
     */
    @Select("SELECT * FROM equipment")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "status", column = "status"),
        @Result(property = "owner", column = "laboratory_id",
            one = @One(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectLaboratoryById")),
        @Result(property = "renter", column = "renter_id",
            one = @One(select = "org.example.laboratory.Mapper.UserMapper.selectUserByIdWithAllRelations")),
        @Result(property = "description", column = "description")
    })
    List<SecurityIssue> selectAllEquipment();

    /**
     * 更新设备信息
     * @param securityIssue 设备对象
     * @return void
     */
    @Update("UPDATE equipment SET name = #{name}, status = #{status}, laboratory_id = #{owner.id}, renter_id = #{renter.id}, description = #{description} WHERE id = #{id}")
    void updateEquipment(SecurityIssue securityIssue);

    /**
     * 根据ID删除设备
     * @param id 设备ID
     * @return void 
     */
    @Delete("DELETE FROM equipment WHERE id = #{id}")
    void deleteEquipment(Integer id);

    /**
     * 根据用户名查找该用户租用的所有设备，并自动填充实验室和租用者及其复杂成员
     * @param username 用户名
     * @return 设备列表
     */
    @Select("SELECT e.* FROM equipment e JOIN user u ON e.renter_id = u.id WHERE u.username = #{username}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "status", column = "status"),
        @Result(property = "owner", column = "laboratory_id",
            one = @One(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectLaboratoryById")),
        @Result(property = "renter", column = "renter_id",
            one = @One(select = "org.example.laboratory.Mapper.UserMapper.selectUserByIdWithAllRelations")),
        @Result(property = "description", column = "description")
    })
    List<SecurityIssue> selectEquipmentByUsername(String username);
} 