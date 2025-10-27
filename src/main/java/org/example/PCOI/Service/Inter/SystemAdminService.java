package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.Log;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SystemAdminService {
    boolean updateUserInfo(Integer userId, String newUsername, String newGender, MultipartFile newAvatar);
    boolean resetPassword(Integer userId);
    List<Log> getLogs();
}

