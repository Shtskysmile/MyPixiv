package org.example.PCOI.Entity;

import lombok.Data;

@Data
public class UserComment {
    private ContributionComment comment; // 评论内容
    private Integer contributionId; // 作品ID
    private String title;          // 作品标题
    private String uploader;       // 上传者用户名
    private String uploaderAvatarPath; // 上传者头像路径
    private String imagePath;      // 作品图片路径
}
