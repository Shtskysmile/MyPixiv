package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.EquipmentMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Service.Inter.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EquipmentServiceImpl implements EquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private UserMapper userMapper;
    @Override
    public List<SecurityIssue> getAllEquipments()
    {
        return equipmentMapper.selectAllEquipment();

    }

    @Override
     public boolean rentEquipments(String username, Integer equipmentId)
    {
        SecurityIssue securityIssue = equipmentMapper.selectEquipmentById(equipmentId);
        if (securityIssue == null || !Objects.equals(securityIssue.getStatus(), "空闲")) {
            return false; // 设备不存在或已被租借
        }
        User user = userMapper.selectUserByName(username);
        securityIssue.setStatus("使用中"); // 设置设备状态为已租借
        securityIssue.setRenter(user);
        equipmentMapper.updateEquipment(securityIssue); // 更新设备状态
        return true;
    }

    @Override
    public boolean backEquipments(Integer equipmentId)
    {
        SecurityIssue securityIssue = equipmentMapper.selectEquipmentById(equipmentId);
        if (securityIssue == null || !Objects.equals(securityIssue.getStatus(), "使用中")) {
            return false; // 设备不存在或未被租借
        }
        securityIssue.setStatus("空闲"); // 设置设备状态为可用
        securityIssue.setRenter(null); // 清除租借人信息
        equipmentMapper.updateEquipment(securityIssue); // 更新设备状态
        return true;
    }

    @Override
    public List<SecurityIssue> getEquipmentByUsername(String username) {

        return equipmentMapper.selectEquipmentByUsername(username);
    }

}
