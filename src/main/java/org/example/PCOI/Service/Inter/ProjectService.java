package org.example.PCOI.Service.Inter;

import java.util.List;

public interface ProjectService {
    boolean createProject(String username,String name,String type,String description, String owner,List<String> users,String startDate, String endDate, Integer LabId);
    List<Project> getAllProjects();  // 获取所有项目列表

    Project getProjectById(int id);  // 根据ID获取项目详情
}


