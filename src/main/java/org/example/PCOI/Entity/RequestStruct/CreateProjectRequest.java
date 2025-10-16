package org.example.PCOI.Entity.RequestStruct;

import lombok.Data;

import java.util.List;

@Data
public class CreateProjectRequest {
    private String Username;
    private String Name;  // 项目名称
    private String Type; // 项目类型
    private String Description; // 项目描述
    private String owner; // 项目所有者的用户名
    private List<String> users; // 项目成员的用户名列表
    private String startDate;     // 项目开始日期
    private String endDate;       // 项目结束日期
    private Integer lab_id; // 实验室ID
}
