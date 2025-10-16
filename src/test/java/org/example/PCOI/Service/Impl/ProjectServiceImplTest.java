package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.LaboratorysMapper;
import org.example.PCOI.Mapper.ProjectMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Mapper.UserProjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceImplTest {
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private LaboratorysMapper laboratorysMapper;
    @Mock
    private UserProjectMapper userProjectMapper;
    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProject_Success() {
        Tag lab = new Tag();
        lab.setStatus("空闲");
        when(laboratorysMapper.selectLaboratoryById(1)).thenReturn(lab);
        User user = new User();
        user.setId(10);
        user.setAuthority(1);
        when(userMapper.selectUserByName("user1")).thenReturn(user);
        doAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(100);
            return null;
        }).when(projectMapper).insertProject(any(Project.class));
        List<String> users = Arrays.asList("user1", "user2");
        boolean result = projectService.createProject("user1", "项目A", "类型A", "描述A", "ownerA", users, "2025-07-01", "2025-07-31", 1);
        assertTrue(result);
        assertEquals("使用中", lab.getStatus());
        verify(projectMapper).insertProject(any(Project.class));
        verify(userProjectMapper).insertUserProject(eq(10), eq(100));
        verify(laboratorysMapper).updateLaboratory(lab);
    }

    @Test
    void testCreateProject_Fail_LabNotFree() {
        Tag lab = new Tag();
        lab.setStatus("使用中");
        when(laboratorysMapper.selectLaboratoryById(1)).thenReturn(lab);
        boolean result = projectService.createProject("user1", "项目A", "类型A", "描述A", "ownerA", Arrays.asList("user1"), "2025-07-01", "2025-07-31", 1);
        assertFalse(result);
        verify(projectMapper, never()).insertProject(any());
    }

    @Test
    void testCreateProject_Fail_UserNoAuthority() {
        Tag lab = new Tag();
        lab.setStatus("空闲");
        when(laboratorysMapper.selectLaboratoryById(1)).thenReturn(lab);
        User user = new User();
        user.setId(10);
        user.setAuthority(3);
        when(userMapper.selectUserByName("user1")).thenReturn(user);
        boolean result = projectService.createProject("user1", "项目A", "类型A", "描述A", "ownerA", Arrays.asList("user1"), "2025-07-01", "2025-07-31", 1);
        assertFalse(result);
        verify(projectMapper, never()).insertProject(any());
    }

    @Test
    void testGetAllProjects() {
        Project project = new Project();
        when(projectMapper.selectAllProjects()).thenReturn(Collections.singletonList(project));
        List<Project> result = projectService.getAllProjects();
        assertEquals(1, result.size());
        verify(projectMapper).selectAllProjects();
    }

    @Test
    void testGetProjectById() {
        Project project = new Project();
        when(projectMapper.selectProjectById(1)).thenReturn(project);
        Project result = projectService.getProjectById(1);
        assertNotNull(result);
        verify(projectMapper).selectProjectById(1);
    }
}

