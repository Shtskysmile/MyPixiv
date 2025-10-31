package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.LogMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.ResponseDTO.R_Log;
import org.example.PCOI.Service.Inter.SystemAdminService;
import org.example.PCOI.Service.Support.FileStorageService;
import org.example.PCOI.Utils.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
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
        try {
            User user = usermapper.selectUserById(userId);
            User existingUser = usermapper.selectUserByName(newUsername);
            if (user != null && (existingUser == null || existingUser.getUserId().equals(userId))
                    && newUsername != null && !newUsername.isEmpty()
                    && newGender != null && (newGender.equals(undefined) || newGender.equals(male) || newGender.equals(female))
                    && newAvatar != null ) {
                user.setUsername(newUsername);
                user.setSex(newGender);
                String avatarUrl = fileStorageService.saveAvatar(newAvatar, userId);
                user.setAvatar(avatarUrl);
                usermapper.updateUser(user);
                return true;
            }
            return false;
        }catch(Exception e){
            log.error("Error updating user info for userId {}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean resetPassword(String userId) {
        try {
            User user = usermapper.selectUserById(userId);
            if (user != null) {
                String defaultPassword = BcryptUtil.hash(defaultPWD);
                user.setPassword(defaultPassword);
                usermapper.updateUser(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error resetting password for userId {}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public List<Log> getLogs() {
        try{
            return logmapper.selectAllLogs();
        }catch (Exception e){
            log.error("Error retrieving logs: {}", e.getMessage());
            return null;
        }
    }
}

