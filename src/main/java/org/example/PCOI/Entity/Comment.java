package org.example.PCOI.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private String commentId;               // 评论ID
    private String description;  // 评论内容
    private LocalDateTime time;         // 评论时间
    private String author;     // 评论者
    private String contribution;  // 被评论作品
}
