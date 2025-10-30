package org.example.PCOI.ResponseDTO;

import lombok.Data;


@Data
public class R_User {
    private String userId;             // 用户ID
    private String role;          // 角色
    private String status;         // 状态
    private String username;     // 用户名
    private String sex;       // 性别
    private String avatar; // 头像路径
}
