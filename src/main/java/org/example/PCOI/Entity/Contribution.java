package org.example.PCOI.Entity;

import lombok.Data;

@Data
public class Contribution {
    private Integer contributionId;              // 作品ID
    private String title;         // 作品标题
    private String image;         // 作品图片路径
    private String description;  // 描述信息
    private String status;        // 状态(封禁状态，正常状态）
    private String publishTime;         // 上传时间
    private String authorId;     // 上传者
    private String uploaderAvatarPath; // 上传者头像路径
    private Integer viewNum;      // 浏览数
    private Integer favoriteCount;        // 收藏数
    private Integer likeCount;      // 点赞数
    private Integer commentCount;   // 评论数
    private String dismissalReason; // 驳回理由
}
