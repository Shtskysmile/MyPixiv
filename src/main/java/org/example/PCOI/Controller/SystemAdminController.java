package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Service.Inter.AdminService;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class SystemAdminController {
    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private LogService logService;

    @PostMapping("/systemAdmin/updateUserInfo")
    public Result<String> updateUserInfo(
            @RequestParam("userId") Integer userId,
            @RequestParam ("newUsername") String newUsername,
            @RequestParam("newGender") String newGender,
            @RequestParam(value = "newAvatar", required = false) MultipartFile newAvatar) {
    }

    @PostMapping("/systemAdmin/resetPassword")
    public Result<String> resetPassword(
            @RequestParam("userId") Integer userId) {
    }

    @GetMapping("/systemAdmin/logs")
    public Result<List<Log>> getLogs() {
    }
}
