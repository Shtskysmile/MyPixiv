package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.User;

import java.util.List;

public interface AdminService {
    List<User> getAllUsers();  // 获取所有用户列表
    List<Log> getAllOperationLogs();  // 获取所有操作日志列表
}
