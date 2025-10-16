package org.example.PCOI.Controller;

import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Entity.RequestStruct.LoginRequest;
import org.example.PCOI.Entity.RequestStruct.RegisterRequest;
import org.example.PCOI.Entity.RequestStruct.UpdatePwdRequest;
import org.example.PCOI.Service.Inter.UserService;
import org.example.PCOI.Service.Inter.LogService;
import org.example.PCOI.Utils.Md5Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private LogService logService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void register_shouldReturnSuccess() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user1");
        req.setPassword("pwd");
        req.setIdentity("student");
        when(userService.findByUserName("user1")).thenReturn(null);
        doNothing().when(logService).logMethodExecution("user1");
        doNothing().when(userService).register(anyString(), anyString(), anyString());
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("注册成功"));
    }

    @Test
    void register_shouldReturnUserExists() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user1");
        req.setPassword("pwd");
        req.setIdentity("student");
        when(userService.findByUserName("user1")).thenReturn(new User());
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("用户已存在"));
    }

    @Test
    void login_shouldReturnSuccess() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("user1");
        req.setPassword("pwd");
        User user = new User();
        user.setUsername("user1");
        user.setPassword(Md5Util.getMD5String("pwd"));
        when(userService.findByUserName("user1")).thenReturn(user);
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("user1"));
    }

    @Test
    void login_shouldReturnUserNotExist() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("user1");
        req.setPassword("pwd");
        when(userService.findByUserName("user1")).thenReturn(null);
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("用户名不存在"));
    }

    @Test
    void login_shouldReturnPasswordError() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("user1");
        req.setPassword("wrongpwd");
        User user = new User();
        user.setUsername("user1");
        user.setPassword(Md5Util.getMD5String("pwd"));
        when(userService.findByUserName("user1")).thenReturn(user);
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    @Test
    void userInfo_shouldReturnSuccess() throws Exception {
        Map<String, String> req = new HashMap<>();
        req.put("username", "user1");
        User user = new User();
        user.setUsername("user1");
        when(userService.findByUserName("user1")).thenReturn(user);
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/userInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("user1"));
    }

    @Test
    void updateUser_shouldReturnSuccess() throws Exception {
        User user = new User();
        user.setUsername("user1");
        user.setIdentity("admin");
        doNothing().when(logService).logMethodExecution("admin");
        doNothing().when(userService).updateUser("user1", "admin");
        mockMvc.perform(post("/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("用户权限更新成功"));
    }

    @Test
    void updatePwd_shouldReturnSuccess() throws Exception {
        UpdatePwdRequest req = new UpdatePwdRequest();
        req.setUsername("user1");
        req.setOldPassword("oldpwd");
        req.setNewPassword("newpwd");
        req.setConfirmPassword("newpwd");
        User user = new User();
        user.setUsername("user1");
        user.setPassword(Md5Util.getMD5String("oldpwd"));
        when(userService.findByUserName("user1")).thenReturn(user);
        doNothing().when(logService).logMethodExecution("user1");
        doNothing().when(userService).updatePwd("user1", "newpwd");
        mockMvc.perform(post("/updatePwd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("密码更新成功"));
    }

    @Test
    void updatePwd_shouldReturnParamError() throws Exception {
        UpdatePwdRequest req = new UpdatePwdRequest();
        req.setUsername("user1");
        req.setOldPassword("");
        req.setNewPassword("");
        req.setConfirmPassword("");
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/updatePwd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("参数不能为空"));
    }

    @Test
    void updatePwd_shouldReturnOldPwdError() throws Exception {
        UpdatePwdRequest req = new UpdatePwdRequest();
        req.setUsername("user1");
        req.setOldPassword("wrongpwd");
        req.setNewPassword("newpwd");
        req.setConfirmPassword("newpwd");
        User user = new User();
        user.setUsername("user1");
        user.setPassword(Md5Util.getMD5String("oldpwd"));
        when(userService.findByUserName("user1")).thenReturn(user);
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/updatePwd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("原密码错误"));
    }

    @Test
    void updatePwd_shouldReturnNewPwdNotMatch() throws Exception {
        UpdatePwdRequest req = new UpdatePwdRequest();
        req.setUsername("user1");
        req.setOldPassword("oldpwd");
        req.setNewPassword("newpwd");
        req.setConfirmPassword("otherpwd");
        User user = new User();
        user.setUsername("user1");
        user.setPassword(Md5Util.getMD5String("oldpwd"));
        when(userService.findByUserName("user1")).thenReturn(user);
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/updatePwd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("新密码不一致"));
    }

    @Test
    void getMyProjects_shouldReturnSuccess() throws Exception {
        Map<String, String> req = new HashMap<>();
        req.put("username", "user1");
        List<Project> projects = Collections.singletonList(new Project());
        when(userService.getmyProjects("user1")).thenReturn(projects);
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/myProjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getMyProjects_shouldReturnError() throws Exception {
        Map<String, String> req = new HashMap<>();
        req.put("username", "user1");
        when(userService.getmyProjects("user1")).thenThrow(new RuntimeException("DB error"));
        doNothing().when(logService).logMethodExecution("user1");
        mockMvc.perform(post("/myProjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error retrieving projects: DB error"));
    }

    @Test
    void getMyLaboratories_shouldReturnSuccess() throws Exception {
        Map<String, Integer> req = new HashMap<>();
        req.put("user_id", 1);
        List<Tag> labs = Collections.singletonList(new Tag());
        when(userService.getLaboratorysByUserId(1)).thenReturn(labs);
        mockMvc.perform(post("/myLaboratories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getMyLaboratories_shouldReturnError() throws Exception {
        Map<String, Integer> req = new HashMap<>();
        req.put("user_id", 1);
        when(userService.getLaboratorysByUserId(1)).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/myLaboratories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error retrieving laboratories: DB error"));
    }

    @Test
    void getMyEquipment_shouldReturnSuccess() throws Exception {
        Map<String, Integer> req = new HashMap<>();
        req.put("userid", 1);
        List<SecurityIssue> securityIssues = Collections.singletonList(new SecurityIssue());
        when(userService.getEquipmentByUserId(1)).thenReturn(securityIssues);
        mockMvc.perform(post("/myEquipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getMyEquipment_shouldReturnError() throws Exception {
        Map<String, Integer> req = new HashMap<>();
        req.put("userid", 1);
        when(userService.getEquipmentByUserId(1)).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/myEquipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error retrieving equipment: DB error"));
    }
}

