package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.LaboratorysMapper;
import org.example.PCOI.Mapper.ProjectMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Mapper.UserProjectMapper;
import org.example.PCOI.Service.Inter.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private  UserMapper userMapper;

    @Autowired
    private LaboratorysMapper laboratorysMapper;
    @Autowired
    private UserProjectMapper userProjectMapper;

    @Override

    public boolean createProject(String username, String name, String type, String description, String owner, List<String> users, String startDate, String endDate,Integer LabId)
    {

        Tag tag = laboratorysMapper.selectLaboratoryById(LabId);
        if(tag !=null&&!tag.getStatus().equals("空闲"))
            return false; // 实验室不存在或已被租借
        User user = userMapper.selectUserByName(username);
        System.out.println("Creating project with user: " + user);
        if(user.getAuthority() == 3) {
            return false;
        }
        Project project = new Project();
        project.setStatus("进行中");
        project.setName(name);
        project.setType(type);
        project.setDescription(description);
        project.setOwner(owner);
        project.setUsers(users);
        project.setStartDate(startDate);
        project.setEndDate(endDate);

        projectMapper.insertProject(project);
        userProjectMapper.insertUserProject(user.getId(),project.getId()); // 添加创建者到项目中
        //System.out.println("Creating project with details: " + project);

        //System.out.println("Selected laboratory: " + tag);
        if (tag != null ) {
            tag.setStatus("使用中"); // 设置实验室状态为已租借
            tag.setProject(project);
            laboratorysMapper.updateLaboratory(tag); // 更新实验室状态
        }
        return true;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectMapper.selectAllProjects();
    }

    @Override
    public Project getProjectById(int id) {
        return projectMapper.selectProjectById(id);
    }




}
