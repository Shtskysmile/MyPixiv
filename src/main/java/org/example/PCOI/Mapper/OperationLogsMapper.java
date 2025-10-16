package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.PCOI.Entity.Log;
import java.util.List;

/**
 * OperationLogsMapper
 * 日志表的数据库操作接口
 */
@Mapper
public interface OperationLogsMapper {
    /**
     * 插入操作日志，操作时间由数据库自动生成
     * @param username 操作用户
     * @param operationType 操作类型（如函数签名）
     * @param ipAddress 操作IP地址
     * @return void
     */
    @Insert("INSERT INTO operation_logs (username, operation_type, ip_address) VALUES (#{username}, #{operationType}, #{ipAddress})")
    void insertLog(@Param("username") String username, @Param("operationType") String operationType, @Param("ipAddress") String ipAddress);

    /**
     * 根据用户名查询该用户的所有操作日志，按时间倒序
     * @param username 用户名
     * @return 日志列表
     */
    @Select("SELECT * FROM operation_logs WHERE username = #{username} ORDER BY timestamp DESC")
    List<Log> selectLogsByUsername(String username);

    /**
     * 查询所有操作日志，按时间倒序
     * @return 日志列表
     */
    @Select("SELECT * FROM operation_logs ORDER BY timestamp DESC")
    List<Log> selectAllLogs();
} 