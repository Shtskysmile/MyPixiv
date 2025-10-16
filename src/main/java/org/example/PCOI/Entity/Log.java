package org.example.PCOI.Entity;

import lombok.Data;

@Data
public class Log {
    private int id;              // 自增ID
    private String username;         // 操作用户
    private String description; // 操作描述
    private String timestamp;     // 操作时间戳
}
