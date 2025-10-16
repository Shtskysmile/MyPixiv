package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.OperationLogsMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Service.Inter.UserService;
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

class AdminServiceImplTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private OperationLogsMapper operationLogsMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user = new User();
        user.setId(1);
        when(userMapper.selectAllUsers()).thenReturn(Collections.singletonList(user));
        when(userService.getLaboratorysByUserId(1)).thenReturn(Arrays.asList());

        List<User> result = adminService.getAllUsers();
        assertEquals(1, result.size());
        verify(userMapper, times(1)).selectAllUsers();
        verify(userService, times(1)).getLaboratorysByUserId(1);
    }

    @Test
    void testGetAllOperationLogs() {
        Log log = new Log();
        when(operationLogsMapper.selectAllLogs()).thenReturn(Collections.singletonList(log));

        List<Log> result = adminService.getAllOperationLogs();
        assertEquals(1, result.size());
        verify(operationLogsMapper, times(1)).selectAllLogs();
    }
}

