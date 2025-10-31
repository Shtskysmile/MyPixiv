package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.PCOI.Entity.Log;

import java.util.List;
@Mapper
public interface LogMapper {
    @Insert("INSERT INTO log (operatorId, description, time) VALUES (#{operatorId}, #{description}, #{time})")
    void insertLog(@Param("operatorId") String operatorId, @Param("description") String description, @Param("time") String time);

    @Select("SELECT * FROM log")
    List<Log> selectAllLogs();

}
