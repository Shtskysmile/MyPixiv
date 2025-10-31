package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.PCOI.Entity.SecurityIssue;

import java.util.List;
@Mapper
public interface SecurityIssueMapper {

    @Insert("INSERT INTO security_issue (userId, description, answer) VALUES (#{userId}, #{description}, #{answer})")
    void insertSecurityIssue(SecurityIssue securityIssue);

    @Select("SELECT * FROM security_issue WHERE userId = #{userId}")
    List<SecurityIssue> selectSecurityIssueByUserId(String userId);
}
