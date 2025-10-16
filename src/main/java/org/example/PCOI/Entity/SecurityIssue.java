package org.example.PCOI.Entity;
import lombok.Data;
@Data
public class SecurityIssue {
    private int id;              // 自增ID
    private String username;         // 所属用户
    private String question;       // 密保问题
    private String answer;        // 密保答案
}
