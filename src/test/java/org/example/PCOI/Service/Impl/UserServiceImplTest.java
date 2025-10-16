package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.LaboratorysMapper;
import org.example.PCOI.Mapper.ProjectMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Utils.Md5Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private LaboratorysMapper laboratorysMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUserName() {
        User user = new User();
        when(userMapper.selectUserByName("testUser")).thenReturn(user);
        User result = userService.findByUserName("testUser");
        assertEquals(user, result);
        verify(userMapper).selectUserByName("testUser");
    }

    @Test
    void testRegister() {
        doNothing().when(userMapper).insertUser(any(User.class));
        userService.register("user1", "pwd123", "学生");
        verify(userMapper).insertUser(any(User.class));
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        when(userMapper.selectUserByName("user1")).thenReturn(user);
        doNothing().when(userMapper).updateUser(user);
        userService.updateUser("user1", "教师");
        verify(userMapper).updateUser(user);
        assertEquals("teacher", user.getIdentity());
        assertEquals(2, user.getAuthority());
    }

    @Test
    void testExistName_True() {
        when(userMapper.selectUserByName("user1")).thenReturn(new User());
        assertTrue(userService.ExistName("user1"));
    }

    @Test
    void testExistName_False() {
        when(userMapper.selectUserByName("user1")).thenReturn(null);
        assertFalse(userService.ExistName("user1"));
    }

    @Test
    void testUpdatePwd() {
        User user = new User();
        when(userMapper.selectUserByName("user1")).thenReturn(user);
        doNothing().when(userMapper).updateUser(user);
        userService.updatePwd("user1", "newPwd");
        verify(userMapper).updateUser(user);
        assertEquals(Md5Util.getMD5String("newPwd"), user.getPassword());
    }

    @Test
    void testGetMyProjects_UserExistsWithProjects() {
        User user = new User();
        user.setId(1);
        when(userMapper.selectUserByName("user1")).thenReturn(user);
        Project project = new Project();
        when(userMapper.selectProjectsByUserId(1)).thenReturn(Collections.singletonList(project));
        List<Project> result = userService.getmyProjects("user1");
        assertEquals(1, result.size());
    }

    @Test
    void testGetMyProjects_UserNotExist() {
        when(userMapper.selectUserByName("user1")).thenReturn(null);
        assertNull(userService.getmyProjects("user1"));
    }

    @Test
    void testGetMyProjects_UserNoProjects() {
        User user = new User();
        user.setId(1);
        when(userMapper.selectUserByName("user1")).thenReturn(user);
        when(userMapper.selectProjectsByUserId(1)).thenReturn(Collections.emptyList());
        assertNull(userService.getmyProjects("user1"));
    }

    @Test
    void testGetLaboratorysByUserId_WithProjectsAndLabs() {
        Project project = new Project();
        project.setId(1);
        when(userMapper.selectProjectsByUserId(1)).thenReturn(Collections.singletonList(project));
        Tag lab = new Tag();
        when(laboratorysMapper.selectLaboratorysByProjectId(1)).thenReturn(Collections.singletonList(lab));
        List<Tag> result = userService.getLaboratorysByUserId(1);
        assertEquals(1, result.size());
    }

    @Test
    void testGetLaboratorysByUserId_NoProjects() {
        when(userMapper.selectProjectsByUserId(1)).thenReturn(Collections.emptyList());
        List<Tag> result = userService.getLaboratorysByUserId(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLaboratorysByUserId_ProjectsNoLabs() {
        Project project = new Project();
        project.setId(1);
        when(userMapper.selectProjectsByUserId(1)).thenReturn(Collections.singletonList(project));
        when(laboratorysMapper.selectLaboratorysByProjectId(1)).thenReturn(Collections.emptyList());
        List<Tag> result = userService.getLaboratorysByUserId(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEquipmentByUserId_UserExistsWithEquipment() {
        User user = new User();
        user.setId(1);
        when(userMapper.selectUserBasicById(1)).thenReturn(user);
        SecurityIssue securityIssue = new SecurityIssue();
        when(userMapper.selectEquipmentByUserId(1)).thenReturn(Collections.singletonList(securityIssue));
        List<SecurityIssue> result = userService.getEquipmentByUserId(1);
        assertEquals(1, result.size());
    }

    @Test
    void testGetEquipmentByUserId_UserNotExist() {
        when(userMapper.selectUserBasicById(1)).thenReturn(null);
        List<SecurityIssue> result = userService.getEquipmentByUserId(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEquipmentByUserId_UserNoEquipment() {
        User user = new User();
        user.setId(1);
        when(userMapper.selectUserBasicById(1)).thenReturn(user);
        when(userMapper.selectEquipmentByUserId(1)).thenReturn(Collections.emptyList());
        List<SecurityIssue> result = userService.getEquipmentByUserId(1);
        assertTrue(result.isEmpty());
    }
}

