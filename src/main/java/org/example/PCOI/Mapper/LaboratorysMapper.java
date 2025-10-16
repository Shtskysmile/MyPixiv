package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.Contribution;

import java.util.List;

/**
 * LaboratorysMapper
 * 实验室表的数据库操作接口
 */
@Mapper
public interface LaboratorysMapper {
    /**
     * 插入新实验室
     * @param laboratory 实验室对象
     * @return void
     */
    @Insert("INSERT INTO laboratorys (id, name, description, manager, status, project_id) VALUES (#{id}, #{name}, #{description}, #{manager}, #{status}, #{project.id})")
    void insertLaboratory(Tag laboratory);

    /**
     * 根据ID查询实验室，并自动填充项目、设备列表、物资列表
     * @param id 实验室ID
     * @return 实验室对象
     */
    @Select("SELECT * FROM laboratorys WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "manager", column = "manager"),
        @Result(property = "status", column = "status"),
        @Result(property = "project", column = "project_id",
            one = @One(select = "org.example.laboratory.Mapper.ProjectMapper.selectProjectBasicById")),
        @Result(property = "equipmentList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectEquipmentByLabId")),
        @Result(property = "suppliesList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectSuppliesByLabId"))
    })
    Tag selectLaboratoryById(Integer id);

    /**
     * 查询所有实验室，并自动填充项目、设备列表、物资列表
     * @return 实验室列表
     */
    @Select("SELECT * FROM laboratorys")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "manager", column = "manager"),
        @Result(property = "status", column = "status"),
        @Result(property = "project", column = "project_id",
            one = @One(select = "org.example.laboratory.Mapper.ProjectMapper.selectProjectBasicById")),
        @Result(property = "equipmentList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectEquipmentByLabId")),
        @Result(property = "suppliesList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectSuppliesByLabId"))
    })
    List<Tag> selectAllLaboratorys();

    /**
     * 更新实验室信息
     * @param laboratory 实验室对象
     * @return 影响的行数
     */
    @Update("UPDATE laboratorys SET name = #{name}, description = #{description}, manager = #{manager}, status = #{status}, project_id = #{project.id} WHERE id = #{id}")
    void updateLaboratory(Tag laboratory);

    /**
     * 根据ID删除实验室
     * @param id 实验室ID
     * @return void
     */
    @Delete("DELETE FROM laboratorys WHERE id = #{id}")
    void deleteLaboratory(Integer id);

    /**
     * 根据项目ID查询实验室，并自动填充项目、设备列表、物资列表
     * @param projectId 项目ID
     * @return 实验室列表 List<Tag>
     */
    @Select("SELECT * FROM laboratorys WHERE project_id = #{projectId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "manager", column = "manager"),
        @Result(property = "status", column = "status"),
        @Result(property = "project", column = "project_id",
            one = @One(select = "org.example.laboratory.Mapper.ProjectMapper.selectProjectBasicById")),
        @Result(property = "equipmentList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectEquipmentByLabId")),
        @Result(property = "suppliesList", column = "id",
            many = @Many(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectSuppliesByLabId"))
    })
    List<Tag> selectLaboratorysByProjectId(Integer projectId);

    /**
     * 根据实验室ID查询实验室基本信息（用于设备关联查询，避免循环引用）
     * @param id 实验室ID
     * @return 实验室基本信息
     */
    @Select("SELECT id, name, description, manager, status FROM laboratorys WHERE id = #{id}")
    Tag selectLaboratoryBasicForEquipment(Integer id);

    /**
     * 根据实验室ID查询设备列表
     * @param labId 实验室ID
     * @return 设备列表
     */
    @Select("SELECT * FROM equipment WHERE laboratory_id = #{labId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "status", column = "status"),
        @Result(property = "description", column = "description"),
        @Result(property = "owner", column = "laboratory_id",
            one = @One(select = "org.example.laboratory.Mapper.LaboratorysMapper.selectLaboratoryBasicForEquipment")),
        @Result(property = "renter", column = "renter_id",
            one = @One(select = "org.example.laboratory.Mapper.UserMapper.selectUserBasicById"))
    })
    List<SecurityIssue> selectEquipmentByLabId(Integer labId);

    /**
     * 根据实验室ID查询物资列表
     * @param labId 实验室ID
     * @return 物资列表
     */
    @Select("SELECT * FROM supplies WHERE laboratory_id = #{labId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "status", column = "status"),
        @Result(property = "description", column = "description"),
        @Result(property = "isLowStock", column = "is_low_stock")
    })
    List<Contribution> selectSuppliesByLabId(Integer labId);
} 