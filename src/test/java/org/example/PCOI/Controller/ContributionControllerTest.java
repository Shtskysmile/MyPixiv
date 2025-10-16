package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.RequestStruct.Rent_BackSuppRequest;
import org.example.PCOI.Service.Inter.SuppliesService;
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

@WebMvcTest(SuppliesController.class)
public class ContributionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SuppliesService suppliesService;
    @MockBean
    private LogService logService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getSupplies_shouldReturnSuccess() throws Exception {
        List<Contribution> supplies = Collections.singletonList(new Contribution());
        when(suppliesService.getAllSupplies()).thenReturn(supplies);
        mockMvc.perform(get("/supplies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getSupplies_shouldReturnError() throws Exception {
        when(suppliesService.getAllSupplies()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/supplies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching equipments: DB error"));
    }

    @Test
    void rentBackSupplies_shouldReturnSuccess() throws Exception {
        Rent_BackSuppRequest req = new Rent_BackSuppRequest();
        req.setUsername("user1");
        req.setId(1);
        req.setNumber(5);
        doNothing().when(logService).logMethodExecution("user1");
        when(suppliesService.updateSupplies(5, 1)).thenReturn(true);
        mockMvc.perform(post("/supplies/rent_backSupplies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("Rent_back SecurityIssue Success!"));
    }

    @Test
    void rentBackSupplies_shouldReturnError() throws Exception {
        Rent_BackSuppRequest req = new Rent_BackSuppRequest();
        req.setUsername("user1");
        req.setId(1);
        req.setNumber(5);
        doNothing().when(logService).logMethodExecution("user1");
        when(suppliesService.updateSupplies(5, 1)).thenReturn(false);
        mockMvc.perform(post("/supplies/rent_backSupplies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Rent_back SecurityIssue Failed"));
    }

    @Test
    void rentBackSupplies_shouldReturnException() throws Exception {
        Rent_BackSuppRequest req = new Rent_BackSuppRequest();
        req.setUsername("user1");
        req.setId(1);
        req.setNumber(5);
        doNothing().when(logService).logMethodExecution("user1");
        when(suppliesService.updateSupplies(5, 1)).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/supplies/rent_backSupplies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching equipments: DB error"));
    }
}

