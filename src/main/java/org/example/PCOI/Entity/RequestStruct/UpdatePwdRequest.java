package org.example.PCOI.Entity.RequestStruct;

import lombok.Data;

@Data
public class UpdatePwdRequest {
    private String oldPassword;  // 用户的旧密码
    private String newPassword;  // 用户的新密码
    private String confirmPassword; // 确认新密码
    private String username;     // 用户名，用于验证用户身份
}
