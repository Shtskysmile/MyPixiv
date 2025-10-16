package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.User;

import java.util.List;

public interface UserService {
    User findByUserName(String username);
    void register(String username, String password,String identity);

    void updateUser(String username,String identity);

    boolean ExistName(String username);

    void updatePwd(String username,String newPwd);

    List<Project> getmyProjects(String username);

    List<Tag> getLaboratorysByUserId(Integer id); // 根据ID获取实验室列表
    List<SecurityIssue> getEquipmentByUserId(Integer id); // 根据ID获取设备列表
}
