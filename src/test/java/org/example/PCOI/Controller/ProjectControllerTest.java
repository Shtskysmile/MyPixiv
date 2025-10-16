package org.example.PCOI.Controller;

import org.example.PCOI.Entity.RequestStruct.CreateProjectRequest;
import org.example.PCOI.Service.Inter.ProjectService;
import org.example.PCOI.Service.Inter.LogService;
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

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private LogService logService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createProject_shouldReturnSuccess() throws Exception {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setUsername("user1");
        req.setName("test project");
        req.setType("type1");
        req.setDescription("desc");
        req.setOwner("owner1");
        req.setUsers(Arrays.asList("user1", "user2"));
        req.setStartDate("2025-07-15");
        req.setEndDate("2025-08-15");
        req.setLab_id(1);
        doNothing().when(logService).logMethodExecution("user1");
        when(projectService.createProject(anyString(), anyString(), anyString(), anyString(), anyString(), anyList(), anyString(), anyString(), anyInt())).thenReturn(true);
        mockMvc.perform(post("/createproject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("Project created successfully!"));
    }

    @Test
    void createProject_shouldReturnAuthorityDenied() throws Exception {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setUsername("user1");
        doNothing().when(logService).logMethodExecution("user1");
        when(projectService.createProject(anyString(), anyString(), anyString(), anyString(), anyString(), anyList(), anyString(), anyString(), anyInt())).thenReturn(false);
        mockMvc.perform(post("/createproject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Authority denied!"));
    }

    @Test
    void createProject_shouldReturnException() throws Exception {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setUsername("user1");
        doNothing().when(logService).logMethodExecution("user1");
        when(projectService.createProject(anyString(), anyString(), anyString(), anyString(), anyString(), anyList(), anyString(), anyString(), anyInt())).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/createproject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Authority denied!"));
    }

    @Test
    void getProjects_shouldReturnSuccess() throws Exception {
        List<Project> projects = Collections.singletonList(new Project());
        when(projectService.getAllProjects()).thenReturn(projects);
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getProjects_shouldReturnError() throws Exception {
        when(projectService.getAllProjects()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching projects: DB error"));
    }

    @Test
    void getProjectById_shouldReturnSuccess() throws Exception {
        Project project = new Project();
        when(projectService.getProjectById(1)).thenReturn(project);
        Map<String, Integer> req = new HashMap<>();
        req.put("id", 1);
        mockMvc.perform(post("/projects/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isMap());
    }

    @Test
    void getProjectById_shouldReturnNotFound() throws Exception {
        when(projectService.getProjectById(1)).thenReturn(null);
        Map<String, Integer> req = new HashMap<>();
        req.put("id", 1);
        mockMvc.perform(post("/projects/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Project not found"));
    }

    @Test
    void getProjectById_shouldReturnError() throws Exception {
        when(projectService.getProjectById(1)).thenThrow(new RuntimeException("DB error"));
        Map<String, Integer> req = new HashMap<>();
        req.put("id", 1);
        mockMvc.perform(post("/projects/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching project: DB error"));
    }
}

