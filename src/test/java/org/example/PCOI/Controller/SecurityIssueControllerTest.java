package org.example.PCOI.Controller;

import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.RequestStruct.Rent_BackEquipRequest;
import org.example.PCOI.Service.Inter.EquipmentService;
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

@WebMvcTest(EquipmentController.class)
public class SecurityIssueControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EquipmentService equipmentService;
    @MockBean
    private LogService logService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getEquipments_shouldReturnSuccess() throws Exception {
        List<SecurityIssue> securityIssues = Collections.singletonList(new SecurityIssue());
        when(equipmentService.getAllEquipments()).thenReturn(securityIssues);
        mockMvc.perform(get("/equipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getEquipments_shouldReturnError() throws Exception {
        when(equipmentService.getAllEquipments()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/equipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching equipments: DB error"));
    }

    @Test
    void rentEquipment_shouldReturnSuccess() throws Exception {
        Rent_BackEquipRequest req = new Rent_BackEquipRequest();
        req.setUsername("user1");
        req.setId(1);
        doNothing().when(logService).logMethodExecution("user1");
        when(equipmentService.rentEquipments("user1", 1)).thenReturn(true);
        mockMvc.perform(post("/equipments/rentEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("Rent SecurityIssue Success!"));
    }

    @Test
    void rentEquipment_shouldReturnError() throws Exception {
        Rent_BackEquipRequest req = new Rent_BackEquipRequest();
        req.setUsername("user1");
        req.setId(1);
        doNothing().when(logService).logMethodExecution("user1");
        when(equipmentService.rentEquipments("user1", 1)).thenReturn(false);
        mockMvc.perform(post("/equipments/rentEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Rent SecurityIssue Failed"));
    }

    @Test
    void rentEquipment_shouldReturnException() throws Exception {
        Rent_BackEquipRequest req = new Rent_BackEquipRequest();
        req.setUsername("user1");
        req.setId(1);
        doNothing().when(logService).logMethodExecution("user1");
        when(equipmentService.rentEquipments("user1", 1)).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/equipments/rentEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching equipments: DB error"));
    }

    @Test
    void backEquipment_shouldReturnSuccess() throws Exception {
        Rent_BackEquipRequest req = new Rent_BackEquipRequest();
        req.setUsername("user1");
        req.setId(1);
        doNothing().when(logService).logMethodExecution("user1");
        when(equipmentService.backEquipments(1)).thenReturn(true);
        mockMvc.perform(post("/myEquipment/backEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("Back SecurityIssue Success!"));
    }

    @Test
    void backEquipment_shouldReturnError() throws Exception {
        Rent_BackEquipRequest req = new Rent_BackEquipRequest();
        req.setUsername("user1");
        req.setId(1);
        doNothing().when(logService).logMethodExecution("user1");
        when(equipmentService.backEquipments(1)).thenReturn(false);
        mockMvc.perform(post("/myEquipment/backEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Back SecurityIssue Failed"));
    }

    @Test
    void backEquipment_shouldReturnException() throws Exception {
        Rent_BackEquipRequest req = new Rent_BackEquipRequest();
        req.setUsername("user1");
        req.setId(1);
        doNothing().when(logService).logMethodExecution("user1");
        when(equipmentService.backEquipments(1)).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/myEquipment/backEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error returning equipment: DB error"));
    }

    @Test
    void getMyEquipments_shouldReturnSuccess() throws Exception {
        Map<String, String> req = new HashMap<>();
        req.put("username", "user1");
        doNothing().when(logService).logMethodExecution("user1");
        List<SecurityIssue> mySecurityIssues = Collections.singletonList(new SecurityIssue());
        when(equipmentService.getEquipmentByUsername("user1")).thenReturn(mySecurityIssues);
        mockMvc.perform(post("/myEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getMyEquipments_shouldReturnError() throws Exception {
        Map<String, String> req = new HashMap<>();
        req.put("username", "user1");
        doNothing().when(logService).logMethodExecution("user1");
        when(equipmentService.getEquipmentByUsername("user1")).thenReturn(null);
        mockMvc.perform(post("/myEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("get myequipments failed "));
    }

    @Test
    void getMyEquipments_shouldReturnException() throws Exception {
        Map<String, String> req = new HashMap<>();
        req.put("username", "user1");
        doNothing().when(logService).logMethodExecution("user1");
        when(equipmentService.getEquipmentByUsername("user1")).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/myEquipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error get myequipments: DB error"));
    }
}

