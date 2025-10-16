package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Service.Inter.AdminService;
import org.example.PCOI.Service.Inter.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(adminController.class)
public class adminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminService adminService;
    @MockBean
    private LogService logService;

    @Test
    void getUsers_shouldReturnSuccess() throws Exception {
        List<User> users = Collections.singletonList(new User());
        when(adminService.getAllUsers()).thenReturn(users);
        doNothing().when(logService).logMethodExecution("admin");
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getUsers_shouldReturnError() throws Exception {
        when(adminService.getAllUsers()).thenThrow(new RuntimeException("DB error"));
        doNothing().when(logService).logMethodExecution("admin");
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching users: DB error"));
    }

    @Test
    void getLogs_shouldReturnSuccess() throws Exception {
        List<Log> logs = Collections.singletonList(new Log());
        when(adminService.getAllOperationLogs()).thenReturn(logs);
        doNothing().when(logService).logMethodExecution("admin");
        mockMvc.perform(get("/admin/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getLogs_shouldReturnError() throws Exception {
        when(adminService.getAllOperationLogs()).thenThrow(new RuntimeException("DB error"));
        doNothing().when(logService).logMethodExecution("admin");
        mockMvc.perform(get("/admin/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching logs: DB error"));
    }
}

