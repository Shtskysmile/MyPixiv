package org.example.PCOI.Entity;
import lombok.Data;

@Data
public class ContributionComment {
    private String author;     // 评论用户
    private String description;      // 评论内容
    private String time;    // 评论时间戳
    private String userAvatarPath; // 用户头像路径
}
