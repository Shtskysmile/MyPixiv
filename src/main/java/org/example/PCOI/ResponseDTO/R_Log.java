package org.example.PCOI.ResponseDTO;

import lombok.Data;

@Data
public class R_Log {
    private String operatorName;         // 操作用户
    private String description; // 操作描述
    private String time;     // 操作时间戳
}
