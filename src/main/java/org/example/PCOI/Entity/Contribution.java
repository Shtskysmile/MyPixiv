package org.example.PCOI.Entity;

import lombok.Data;

@Data
public class Contribution {
    private Integer contributionId;              // 作品ID
    private String title;         // 作品标题
    private String imagePath;         // 作品图片路径
    private String description;  // 描述信息
    private String state;        // 状态(封禁状态，正常状态）
    private String time;         // 上传时间
    private String uploader;     // 上传者
    private String uploaderAvatarPath; // 上传者头像路径
    private Integer viewNum;      // 浏览数
    private Integer favourNum;        // 收藏数
    private Integer likeNum;      // 点赞数
    private Integer commentNum;   // 评论数
    private String dismissReason; // 驳回理由
}
