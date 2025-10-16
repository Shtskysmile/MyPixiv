package org.example.PCOI.Controller;

import org.example.PCOI.Entity.*;
import org.example.PCOI.Entity.RequestStruct.LoginRequest;
import org.example.PCOI.Entity.RequestStruct.RegisterRequest;
import org.example.PCOI.Entity.RequestStruct.UpdatePwdRequest;
import org.example.PCOI.Service.Inter.LogService;
import org.example.PCOI.Service.Inter.UserService;
import org.example.PCOI.Utils.JwtUtil;
import org.example.PCOI.Utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
public class UserController {
    @Autowired
    private UserService userservice;
    @Autowired
    private LogService logService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest req){
        String username = req.getUsername();
        String password = req.getPassword();
        String identity = req.getIdentity();
        logService.logMethodExecution(username);
        //查询用户,判断用户是否存在
        User u=userservice.findByUserName(username);
        //注册
        if(u==null) {
            userservice.register(username,password,identity);
            return Result.success("注册成功");
        }else{
            return Result.error("用户已存在");
        }
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest req){
        String username = req.getUsername();
        String password = req.getPassword();
        logService.logMethodExecution(username);
        //查询用户
        User loginuser=userservice.findByUserName(username);
        //判断用户是否存在
        if(loginuser==null){
            return Result.error("用户名不存在");
        }
        //判断密码是否正确
        if(Md5Util.getMD5String(password).equals(loginuser.getPassword())) {
            Map<String,Object> claims=new HashMap<>();
            claims.put("id",loginuser.getId());
            claims.put("username",loginuser.getUsername());
            String token= JwtUtil.genToken(claims);
            return Result.success(loginuser);
        }
        return Result.error("密码错误");
    }
    //用户详细信息
    @PostMapping("/userInfo")
    public Result<User> userInfo(@RequestBody Map<String, String> req){
        String username = req.get("username");
        logService.logMethodExecution(username);
        User user = userservice.findByUserName(username);
        return Result.success(user);
    }

    @PostMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        System.out.println("Received request to update user: " + user);
        String username = user.getUsername();
        logService.logMethodExecution("admin");
        String identity = user.getIdentity();
        userservice.updateUser(username, identity);
        return Result.success("用户权限更新成功");
    }


    @PostMapping("/updatePwd")
    public Result<String> updatePwd(@RequestBody UpdatePwdRequest params) {
        String username = params.getUsername();
        String oldPwd = params.getOldPassword();
        String newPwd = params.getNewPassword();
        String rePwd = params.getConfirmPassword();
        logService.logMethodExecution(username);
        if(!StringUtils.hasLength(oldPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd)){
            return Result.error("参数不能为空");
        }
        //原密码是否则正确
        User loginUser=userservice.findByUserName(username);
        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码错误");
        }

        //新密码是否一致
        if(!newPwd.equals(rePwd)){
            return Result.error("新密码不一致");
        }
        userservice.updatePwd(username, newPwd);
        return Result.success("密码更新成功");
    }

    @PostMapping("/myProjects")
    public Result<List<Project>> getMyProjects(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        logService.logMethodExecution(username);
        try {
            List<Project> projects = userservice.getmyProjects(username);

            return Result.success(projects);
        } catch (Exception e) {
            return Result.error("Error retrieving projects: " + e.getMessage());
        }
    }

    @PostMapping("/myLaboratories")
    public Result<List<Tag>> getMyLaboratories(@RequestBody Map<String, Integer> req) {
        Integer user_id = req.get("user_id");
        try {
            List<Tag> laboratories = userservice.getLaboratorysByUserId(user_id);
            System.out.println("Retrieved laboratories: " + laboratories);
            return Result.success(laboratories);
        } catch (Exception e) {
            return Result.error("Error retrieving laboratories: " + e.getMessage());
        }
    }

    @PostMapping("/myEquipments")
    public Result<List<SecurityIssue>> getMyEquipment(@RequestBody Map<String,Integer> req) {
        Integer user_id = req.get("userid");
        try {
            List<SecurityIssue> securityIssueList = userservice.getEquipmentByUserId(user_id);
            return Result.success(securityIssueList);
        } catch (Exception e) {
            return Result.error("Error retrieving equipment: " + e.getMessage());
        }
    }

}
