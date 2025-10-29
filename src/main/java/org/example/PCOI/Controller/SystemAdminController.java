package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Service.Inter.SystemAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class SystemAdminController {
    @Autowired
    private SystemAdminService systemAdminService;

    @PostMapping("/systemAdmin/updateUserInfo")
    public Result<String> updateUserInfo(
            @RequestParam("userId") Integer userId,
            @RequestParam ("newUsername") String newUsername,
            @RequestParam("newGender") String newGender,
            @RequestParam(value = "newAvatar", required = false) MultipartFile newAvatar) {
        try {
            boolean ok = systemAdminService.updateUserInfo(userId, newUsername, newGender, newAvatar);
            if (ok) {
                return Result.success("更新用户信息成功");
            }
            return Result.error("更新用户信息失败");
        } catch (Exception e) {
            return Result.error("更新用户信息出错: " + e.getMessage());
        }
    }

    @PostMapping("/systemAdmin/resetPassword")
    public Result<String> resetPassword(
            @RequestParam("userId") Integer userId) {
        try {
            boolean ok = systemAdminService.resetPassword(userId);
            if (ok) {
                return Result.success("重置密码成功");
            }
            return Result.error("重置密码失败");
        } catch (Exception e) {
            return Result.error("重置密码出错: " + e.getMessage());
        }
    }

    @GetMapping("/systemAdmin/logs")
    public Result<List<Log>> getLogs() {
        try {
            List<Log> logs = systemAdminService.getLogs();
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("获取日志出错: " + e.getMessage());
        }
    }
}
