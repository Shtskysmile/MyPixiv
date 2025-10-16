package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.EquipmentMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityIssueServiceImplTest {
    @Mock
    private EquipmentMapper equipmentMapper;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEquipments() {
        SecurityIssue securityIssue = new SecurityIssue();
        when(equipmentMapper.selectAllEquipment()).thenReturn(Collections.singletonList(securityIssue));
        List<SecurityIssue> result = equipmentService.getAllEquipments();
        assertEquals(1, result.size());
        verify(equipmentMapper, times(1)).selectAllEquipment();
    }

    @Test
    void testRentEquipments_Success() {
        SecurityIssue securityIssue = new SecurityIssue();
        securityIssue.setStatus("空闲");
        when(equipmentMapper.selectEquipmentById(1)).thenReturn(securityIssue);
        User user = new User();
        when(userMapper.selectUserByName("testUser")).thenReturn(user);
        boolean result = equipmentService.rentEquipments("testUser", 1);
        assertTrue(result);
        assertEquals("使用中", securityIssue.getStatus());
        assertEquals(user, securityIssue.getRenter());
        verify(equipmentMapper).updateEquipment(securityIssue);
    }

    @Test
    void testRentEquipments_Fail_AlreadyRented() {
        SecurityIssue securityIssue = new SecurityIssue();
        securityIssue.setStatus("使用中");
        when(equipmentMapper.selectEquipmentById(1)).thenReturn(securityIssue);
        boolean result = equipmentService.rentEquipments("testUser", 1);
        assertFalse(result);
        verify(equipmentMapper, never()).updateEquipment(any());
    }

    @Test
    void testBackEquipments_Success() {
        SecurityIssue securityIssue = new SecurityIssue();
        securityIssue.setStatus("使用中");
        securityIssue.setRenter(new User());
        when(equipmentMapper.selectEquipmentById(1)).thenReturn(securityIssue);
        boolean result = equipmentService.backEquipments(1);
        assertTrue(result);
        assertEquals("空闲", securityIssue.getStatus());
        assertNull(securityIssue.getRenter());
        verify(equipmentMapper).updateEquipment(securityIssue);
    }

    @Test
    void testBackEquipments_Fail_NotRented() {
        SecurityIssue securityIssue = new SecurityIssue();
        securityIssue.setStatus("空闲");
        when(equipmentMapper.selectEquipmentById(1)).thenReturn(securityIssue);
        boolean result = equipmentService.backEquipments(1);
        assertFalse(result);
        verify(equipmentMapper, never()).updateEquipment(any());
    }

    @Test
    void testGetEquipmentByUsername() {
        SecurityIssue securityIssue = new SecurityIssue();
        when(equipmentMapper.selectEquipmentByUsername("testUser")).thenReturn(Collections.singletonList(securityIssue));
        List<SecurityIssue> result = equipmentService.getEquipmentByUsername("testUser");
        assertEquals(1, result.size());
        verify(equipmentMapper, times(1)).selectEquipmentByUsername("testUser");
    }
}

