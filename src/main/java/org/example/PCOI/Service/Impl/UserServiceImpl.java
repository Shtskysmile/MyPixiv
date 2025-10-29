package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.LaboratorysMapper;
import org.example.PCOI.Mapper.ProjectMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Service.Inter.UserService;
import org.example.PCOI.Utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper usermapper;
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private LaboratorysMapper laboratorsmapper;

    @Override
    public User findByUserName(String username) {
        // 实现根据用户名查找用户的逻辑
        User user = usermapper.selectUserByName(username);
        return user;
    }

    @Override
    public void register(String username, String password, String identity) {
        int authority = switch (identity) {
            case "学生" -> {
                identity = "student";
                yield 3;
            }
            case "教师" -> {
                identity = "teacher";
                yield 2;
            }
            case "管理员" -> {
                identity = "admin";
                yield 1;
            }
            default -> throw new IllegalArgumentException("未知身份类型: " + identity);
        };
        String pwd_password= Md5Util.getMD5String(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(pwd_password);
        user.setIdentity(identity);
        user.setAuthority(authority);
        usermapper.insertUser(user);

    }

    @Override
    public Map<String,Object> login(String username, String password) {
        String md5String = Md5Util.getMD5String(password);
        User user = usermapper.selectPasswordByUserName(username);
        if (user != null && user.getPassword().equals(md5String)) {
            // 登录成功，返回用户信息
            return Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "identity", user.getIdentity(),
                    "authority", user.getAuthority()
            );
        } else {
            // 登录失败，返回 null
            return null;
        }
    }

    @Override
    public void updateUser(String username, String identity) {
        // 获取当前用户的ID
        User user = usermapper.selectUserByName(username);

        int authority = switch (identity) {
            case "学生" -> {
                identity = "student";
                yield 3;
            }
            case "教师" -> {
                identity = "teacher";
                yield 2;
            }
            case "管理员" -> {
                identity = "admin";
                yield 1;
            }
            default -> throw new IllegalArgumentException("未知身份类型: " + identity);
        };
        user.setIdentity(identity);
        user.setAuthority(authority);

        usermapper.updateUser(user);
    }

    @Override
    public boolean ExistName(String username) {
        // 实现检查用户名是否存在的逻辑
        User user = usermapper.selectUserByName(username);
        return user != null;
    }

    @Override
    public void updatePwd(String username, String newPwd) {
        // 实现更新密码的逻辑
        String md5String = Md5Util.getMD5String(newPwd);
        User user = usermapper.selectUserByName(username);
        user.setPassword(md5String);
            usermapper.updateUser(user);

    }

    @Override
    public List<Project> getmyProjects(String username)
    {
        // 获取当前用户的ID
        User user = usermapper.selectUserByName(username);
        if (user == null) {
            return null; // 如果用户不存在，返回null
        }
        List<Project> projects = usermapper.selectProjectsByUserId(user.getId());
        if (projects != null && !projects.isEmpty()) {
            return projects; // 返回用户的项目列表
        } else {
            return null; // 如果没有项目，返回null
        }
    }

    @Override
    public List<Tag> getLaboratorysByUserId(Integer id) {

        List<Project> projects = usermapper.selectProjectsByUserId(id);
        if (projects.isEmpty()) {
            return List.of(); // 返回空列表而不是 null
        }
        List<Tag> laboratories = new ArrayList<>();
        for (Project project : projects) {
            laboratories.addAll(laboratorsmapper.selectLaboratorysByProjectId(project.getId()));
        }

        if (laboratories.isEmpty()) {
            return List.of(); // 返回空列表而不是 null
        }
        return laboratories;
        }
    @Override
    public List<SecurityIssue> getEquipmentByUserId(Integer id) {
        // 获取当前用户的ID
        User user = usermapper.selectUserBasicById(id);
        if (user == null) {
            return List.of(); // 返回空列表而不是 null
        }
        List<SecurityIssue> securityIssueList = usermapper.selectEquipmentByUserId(user.getId());
        if (securityIssueList != null && !securityIssueList.isEmpty()) {
            return securityIssueList; // 返回用户的设备列表
        } else {
            return List.of(); // 如果没有设备，返回空列表
        }
    }
}
