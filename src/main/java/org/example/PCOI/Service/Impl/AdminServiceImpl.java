package org.example.PCOI.Service.Impl;


import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.OperationLogsMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Service.Inter.AdminService;
import org.example.PCOI.Service.Inter.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OperationLogsMapper operationlogsmapper;

    @Autowired
    private UserService userService;

    @Override
    public List<User> getAllUsers()
    {
        // 实现获取所有用户的逻辑
        List<User> users = userMapper.selectAllUsers();
        for (User user : users) {
            user.setLaboratorysList(userService.getLaboratorysByUserId(user.getId()));
        }
        return users;
    }

    @Override
    public List<Log> getAllOperationLogs()
    {
        return  operationlogsmapper.selectAllLogs();
    }
}
