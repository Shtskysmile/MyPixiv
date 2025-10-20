package org.example.PCOI.Entity;

import lombok.Data;


@Data
public class User {
    private String userId;             // 用户ID
    private String username;     // 用户名
    private String gender;       // 性别
    private String avatarPath; // 头像路径
}
