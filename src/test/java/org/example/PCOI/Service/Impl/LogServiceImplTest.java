package org.example.PCOI.Service.Impl;

import org.example.PCOI.Mapper.OperationLogsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class LogServiceImplTest {
    @Mock
    private OperationLogsMapper operationLogsMapper;
    @InjectMocks
    private LogServiceImpl logService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogMethodExecution() {
        String username = "testUser";
        logService.logMethodExecution(username);
        verify(operationLogsMapper, times(1)).insertLog(eq(username), anyString(), eq("192.168.80.16"));
    }
}

