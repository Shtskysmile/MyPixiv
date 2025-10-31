package org.example.PCOI.ResponseDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class R_Log {
    private String operatorId;         // 操作用户
    private String description; // 操作描述
    private LocalDateTime time;     // 操作时间戳
}
