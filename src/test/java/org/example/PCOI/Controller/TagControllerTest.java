package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.RequestStruct.BackLaboratoryRequest;
import org.example.PCOI.Service.Inter.LaboratorysService;
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

@WebMvcTest(LaboratorysController.class)
public class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LaboratorysService laboratorysService;
    @MockBean
    private LogService logService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getLaboratorys_shouldReturnSuccess() throws Exception {
        List<Tag> labs = Collections.singletonList(new Tag());
        when(laboratorysService.getLaboratorys()).thenReturn(labs);
        mockMvc.perform(get("/laboratorys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getLaboratorys_shouldReturnError() throws Exception {
        when(laboratorysService.getLaboratorys()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/laboratorys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching laboratorys: DB error"));
    }

    @Test
    void getLaboratorysById_shouldReturnSuccess() throws Exception {
        Tag lab = new Tag();
        when(laboratorysService.getLaboratoryById(1)).thenReturn(lab);
        Map<String, Integer> req = new HashMap<>();
        req.put("lab_id", 1);
        mockMvc.perform(get("/laboratorys/laboratory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isMap());
    }

    @Test
    void getLaboratorysById_shouldReturnNotFound() throws Exception {
        when(laboratorysService.getLaboratoryById(1)).thenReturn(null);
        Map<String, Integer> req = new HashMap<>();
        req.put("lab_id", 1);
        mockMvc.perform(get("/laboratorys/laboratory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Laboratory not found"));
    }

    @Test
    void getLaboratorysById_shouldReturnError() throws Exception {
        when(laboratorysService.getLaboratoryById(1)).thenThrow(new RuntimeException("DB error"));
        Map<String, Integer> req = new HashMap<>();
        req.put("lab_id", 1);
        mockMvc.perform(get("/laboratorys/laboratory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error fetching laboratory: DB error"));
    }

    @Test
    void backLaboratory_shouldReturnSuccess() throws Exception {
        BackLaboratoryRequest req = new BackLaboratoryRequest();
        req.setLab_id(1);
        req.setUsername("user1");
        doNothing().when(logService).logMethodExecution("user1");
        when(laboratorysService.backLaboratory(1)).thenReturn(true);
        mockMvc.perform(post("/mylaboratorys/backlaboratory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("Back Laboratory Success!"));
    }

    @Test
    void backLaboratory_shouldReturnError() throws Exception {
        BackLaboratoryRequest req = new BackLaboratoryRequest();
        req.setLab_id(1);
        req.setUsername("user1");
        doNothing().when(logService).logMethodExecution("user1");
        when(laboratorysService.backLaboratory(1)).thenReturn(false);
        mockMvc.perform(post("/mylaboratorys/backlaboratory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Back Laboratory Failed"));
    }

    @Test
    void backLaboratory_shouldReturnException() throws Exception {
        BackLaboratoryRequest req = new BackLaboratoryRequest();
        req.setLab_id(1);
        req.setUsername("user1");
        doNothing().when(logService).logMethodExecution("user1");
        when(laboratorysService.backLaboratory(1)).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/mylaboratorys/backlaboratory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("Error returning laboratory: DB error"));
    }
}

