package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.PCOI.Entity.Contribution;

import java.util.List;

/**
 * SuppliesMapper
 * 物资表的数据库操作接口
 */
@Mapper
public interface SuppliesMapper {
    /**
     * 插入新物资
     * @param contribution 物资对象
     * @return void
     */
    @Insert("INSERT INTO supplies (id, name, type, quantity, laboratory_id, renter_id, status, description, is_low_stock) VALUES (#{id}, #{name}, #{type}, #{quantity}, #{owner.id}, #{renter.id}, #{status}, #{description}, #{isLowStock})")
    void insertSupplies(Contribution contribution);

    /**
     * 根据ID查询物资，并递归自动填充实验室和租用者及其复杂成员
     * @param id 物资ID
     * @return 物资对象
     */
    @Select("SELECT * FROM supplies WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "owner", column = "laboratory_id",
            one = @One(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectLaboratoryById")),
        @Result(property = "renter", column = "renter_id",
            one = @One(select = "org.example.laboratory.Mapper.UserMapper.selectUserByIdWithAllRelations")),
        @Result(property = "status", column = "status"),
        @Result(property = "description", column = "description"),
        @Result(property = "isLowStock", column = "is_low_stock")
    })
    Contribution selectSuppliesById(Integer id);

    /**
     * 查询所有物资，并递归自动填充实验室和租用者及其复杂成员
     * @return 物资列表
     */
    @Select("SELECT * FROM supplies")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "owner", column = "laboratory_id",
            one = @One(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectLaboratoryById")),
        @Result(property = "renter", column = "renter_id",
            one = @One(select = "org.example.laboratory.Mapper.UserMapper.selectUserByIdWithAllRelations")),
        @Result(property = "status", column = "status"),
        @Result(property = "description", column = "description"),
        @Result(property = "isLowStock", column = "is_low_stock")
    })
    List<Contribution> selectAllSupplies();

    /**
     * 更新物资信息
     * @param contribution 物资对象
     * @return void
     */
    @Update("UPDATE supplies SET name = #{name}, type = #{type}, quantity = #{quantity}, laboratory_id = #{owner.id}, renter_id = #{renter.id}, status = #{status}, description = #{description}, is_low_stock = #{isLowStock} WHERE id = #{id}")
    void updateSupplies(Contribution contribution);

    /**
     * 根据ID删除物资
     * @param id 物资ID
     * @return void
     */
    @Delete("DELETE FROM supplies WHERE id = #{id}")
    void deleteSupplies(Integer id);

    /**
     * 根据用户名查找该用户租用的所有物资，并递归自动填充实验室和租用者及其复杂成员
     * @param username 用户名
     * @return 物资列表
     */
    @Select("SELECT s.* FROM supplies s JOIN user u ON s.renter_id = u.id WHERE u.username = #{username}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "owner", column = "laboratory_id",
            one = @One(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectLaboratoryById")),
        @Result(property = "renter", column = "renter_id",
            one = @One(select = "org.example.laboratory.Mapper.UserMapper.selectUserByIdWithAllRelations")),
        @Result(property = "status", column = "status"),
        @Result(property = "description", column = "description"),
        @Result(property = "isLowStock", column = "is_low_stock")
    })
    List<Contribution> selectSuppliesByUsername(String username);
} 