package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.SecurityIssue;

import java.util.List;

public interface EquipmentService {
    List<SecurityIssue> getAllEquipments();  // 获取所有设备列表
    boolean rentEquipments(String username, Integer equipmentId);  // 租借设备
    boolean backEquipments(Integer equipmentId);  // 归还设备
    List<SecurityIssue> getEquipmentByUsername(String username);  // 根据ID获取设备详情
}
