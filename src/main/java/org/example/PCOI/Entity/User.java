package org.example.PCOI.Entity;

import lombok.Data;


@Data
public class User {
    private Integer userId;             // 用户ID
    private String role;          // 角色
    private String state;         // 状态
    private String username;     // 用户名
    private String gender;       // 性别
    private String avatarPath; // 头像路径
}
