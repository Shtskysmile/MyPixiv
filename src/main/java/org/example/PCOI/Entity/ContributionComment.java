package org.example.PCOI.Entity;
import lombok.Data;

@Data
public class ContributionComment {
    private String username;     // 评论用户
    private String commentContent;      // 评论内容
    private String timeStamp;    // 评论时间戳
    private String userAvatarPath; // 用户头像路径
}
