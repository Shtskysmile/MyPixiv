package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.ResponseDTO.Result;
import org.example.PCOI.Service.Inter.SystemAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class SystemAdminController {
    @Autowired
    private SystemAdminService systemAdminService;

    /**
     * 系统管理员更新用户信息
     * 路径: POST /systemAdmin/updateUserInfo
     * 说明: 由系统管理员根据 userId 修改指定用户的用户名、性别及头像。
     *
     * 参数:
     * - userId: 目标用户的唯一标识(必填)
     * - newUsername: 新用户名(必填)
     * - newGender: 新性别(必填)。约定: 0=未知, 1=男, 2=女
     * - newAvatar: 新头像文件(可选)，multipart/form-data 的文件字段名为 newAvatar
     *
     * 返回:
     * - 成功: Result.success("更新用户信息成功")
     * - 失败: Result.error("更新用户信息失败")
     */
    @PostMapping("/systemAdmin/updateUserInfo")
    public Result<String> updateUserInfo(
            @RequestParam("userId") String userId,
            @RequestParam ("newUsername") String newUsername,
            @RequestParam("newGender") Integer newGender,
            @RequestPart(name = "newAvatar", required = false) MultipartFile newAvatar) {
        // 调用服务层执行业务更新：用户名/性别/头像(头像可为空)
        boolean ok = systemAdminService.updateUserInfo(userId, newUsername, newGender, newAvatar);
        // 根据业务结果返回统一响应
        if (ok) {
            return Result.success("更新用户信息成功");
        }
        return Result.error("更新用户信息失败");
    }

    @PostMapping("/systemAdmin/resetPassword")
    public Result<String> resetPassword(
            @RequestParam("userId") String userId) {
        boolean ok = systemAdminService.resetPassword(userId);
        if (ok) {
            return Result.success("重置密码成功");
        }
        return Result.error("重置密码失败");
    }

    @GetMapping("/systemAdmin/logs")
    public Result<List<Log>> getLogs() {
        List<Log> logs = systemAdminService.getLogs();
        return Result.success(logs);
    }
}
