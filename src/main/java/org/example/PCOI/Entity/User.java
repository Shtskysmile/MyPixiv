package org.example.PCOI.Entity;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private int id;              // 自增ID
    private String username;     // 用户名
    private String password;     // 密码
    private String gender;       // 性别
    private String state;        // 状态(封禁状态，正常状态）
    private String avatarPath; // 头像路径
}
