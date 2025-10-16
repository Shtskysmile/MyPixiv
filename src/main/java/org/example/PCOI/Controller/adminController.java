package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Service.Inter.AdminService;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class adminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private LogService logService;

    @GetMapping("/admin")
    public Result<List<User>> getUsers() {
        logService.logMethodExecution("admin");
       try {
           List<User> users = adminService.getAllUsers();
           return Result.success(users);
       } catch (Exception e) {
           return Result.error("Error fetching users: " + e.getMessage());
       }
    }

    @GetMapping("/admin/logs")
    public Result<List<Log>> getLogs() {
        logService.logMethodExecution("admin");
        try {
            List<Log> logs = adminService.getAllOperationLogs();
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("Error fetching logs: " + e.getMessage());
        }
    }
}
