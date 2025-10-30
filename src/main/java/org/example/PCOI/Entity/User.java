package org.example.PCOI.Entity;

import lombok.Data;


@Data
public class User {
    private String userId;             // 用户ID
    private String password;        // 密码
    private String role;          // 角色
    private String status;         // 状态
    private String username;     // 用户名
    private String sex;       // 性别
    private String avatar; // 头像路径
}
