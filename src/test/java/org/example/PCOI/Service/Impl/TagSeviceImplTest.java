package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Mapper.LaboratorysMapper;
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

class TagSeviceImplTest {
    @Mock
    private LaboratorysMapper laboratorysMapper;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private LaboratorysSeviceImpl laboratorysService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLaboratorys() {
        Tag lab = new Tag();
        when(laboratorysMapper.selectAllLaboratorys()).thenReturn(Collections.singletonList(lab));
        List<Tag> result = laboratorysService.getLaboratorys();
        assertEquals(1, result.size());
        verify(laboratorysMapper, times(1)).selectAllLaboratorys();
    }

    @Test
    void testGetLaboratoryById() {
        Tag lab = new Tag();
        when(laboratorysMapper.selectLaboratoryById(1)).thenReturn(lab);
        Tag result = laboratorysService.getLaboratoryById(1);
        assertNotNull(result);
        verify(laboratorysMapper, times(1)).selectLaboratoryById(1);
    }

    @Test
    void testBackLaboratory_Success() {
        Tag lab = new Tag();
        lab.setStatus("使用中");
        when(laboratorysMapper.selectLaboratoryById(1)).thenReturn(lab);
        boolean result = laboratorysService.backLaboratory(1);
        assertTrue(result);
        assertEquals("空闲", lab.getStatus());
        assertNull(lab.getProject());
        verify(laboratorysMapper).updateLaboratory(lab);
    }

    @Test
    void testBackLaboratory_Fail_NotExist() {
        when(laboratorysMapper.selectLaboratoryById(1)).thenReturn(null);
        boolean result = laboratorysService.backLaboratory(1);
        assertFalse(result);
        verify(laboratorysMapper, never()).updateLaboratory(any());
    }

    @Test
    void testBackLaboratory_Fail_NotRented() {
        Tag lab = new Tag();
        lab.setStatus("空闲");
        when(laboratorysMapper.selectLaboratoryById(1)).thenReturn(lab);
        boolean result = laboratorysService.backLaboratory(1);
        assertFalse(result);
        verify(laboratorysMapper, never()).updateLaboratory(any());
    }
}

