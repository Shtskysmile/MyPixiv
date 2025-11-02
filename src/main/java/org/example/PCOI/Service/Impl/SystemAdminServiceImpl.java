package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.LogMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Service.Inter.SystemAdminService;
import org.example.PCOI.Service.Support.FileStorageService;
import org.example.PCOI.Utils.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.example.PCOI.Service.Support.Enum.*;
@Slf4j
@Service
public class SystemAdminServiceImpl implements SystemAdminService {
    @Autowired
    private UserMapper usermapper;
    @Autowired
    private LogMapper logmapper;
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public boolean updateUserInfo(String userId, String newUsername, Integer newGender, MultipartFile newAvatar) {
        User user = usermapper.selectUserById(userId);
        if(user == null) {
            return false; // 用户不存在
        }
        if(newUsername!=null && !newUsername.isEmpty()) {
            User existingUser = usermapper.selectUserByName(newUsername);
            if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                return false; // 新用户名已被其他用户使用
            }
            user.setUsername(newUsername);
        }
        if(newGender != null) {
            user.setSex(newGender);
        }
        if(newAvatar != null && !newAvatar.isEmpty()) {
            String avatarUrl = fileStorageService.saveAvatar(newAvatar,userId);
            user.setAvatar(avatarUrl);
        }
        usermapper.updateUser(user);
        return true;
    }

    @Override
    public boolean resetPassword(String userId) {
        User user = usermapper.selectUserById(userId);
        if (user != null) {
            String defaultPassword = BcryptUtil.hash(defaultPWD);
            user.setPassword(defaultPassword);
            usermapper.updateUser(user);
            return true;
        }
        return false;
    }

    @Override
    public List<Log> getLogs() {
        return logmapper.selectAllLogs();
    }
}

