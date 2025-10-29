package org.example.PCOI.Entity;

import lombok.Data;

@Data
public class Log {
    private String operatorName;         // 操作用户
    private String description; // 操作描述
    private String time;     // 操作时间戳
}
