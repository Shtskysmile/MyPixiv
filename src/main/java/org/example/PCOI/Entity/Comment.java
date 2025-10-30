package org.example.PCOI.Entity;

import lombok.Data;

@Data
public class Comment {
    private String description;  // 评论内容
    private String time;         // 评论时间
    private String author;     // 评论者
    private String contribution;  // 被评论作品
}
