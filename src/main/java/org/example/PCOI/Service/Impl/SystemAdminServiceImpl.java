package org.example.PCOI.Service.Impl;

import org.example.PCOI.ResponseDTO.R_Log;
import org.example.PCOI.Service.Inter.SystemAdminService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Service
public class SystemAdminServiceImpl implements SystemAdminService {
    @Override
    public boolean updateUserInfo(Integer userId, String newUsername, String newGender, MultipartFile newAvatar) {
        // TODO: 系统管理员更新用户信息
        return false;
    }

    @Override
    public boolean resetPassword(Integer userId) {
        // TODO: 系统管理员重置用户密码
        return false;
    }

    @Override
    public List<R_Log> getLogs() {
        // TODO: 查询系统日志
        return Collections.emptyList();
    }
}

