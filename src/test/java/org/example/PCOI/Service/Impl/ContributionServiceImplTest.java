package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Mapper.SuppliesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContributionServiceImplTest {
    @Mock
    private SuppliesMapper suppliesMapper;
    @InjectMocks
    private SuppliesServiceImpl suppliesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSupplies() {
        Contribution contribution = new Contribution();
        when(suppliesMapper.selectAllSupplies()).thenReturn(Collections.singletonList(contribution));
        List<Contribution> result = suppliesService.getAllSupplies();
        assertEquals(1, result.size());
        verify(suppliesMapper, times(1)).selectAllSupplies();
    }

    @Test
    void testUpdateSupplies_Success() {
        Contribution contribution = new Contribution();
        contribution.setQuantity(10);
        when(suppliesMapper.selectSuppliesById(1)).thenReturn(contribution);
        boolean result = suppliesService.updateSupplies(5, 1);
        assertTrue(result);
        assertEquals(15, contribution.getQuantity());
        verify(suppliesMapper).updateSupplies(contribution);
    }

    @Test
    void testUpdateSupplies_Fail_NotExist() {
        when(suppliesMapper.selectSuppliesById(1)).thenReturn(null);
        boolean result = suppliesService.updateSupplies(5, 1);
        assertFalse(result);
        verify(suppliesMapper, never()).updateSupplies(any());
    }
}

