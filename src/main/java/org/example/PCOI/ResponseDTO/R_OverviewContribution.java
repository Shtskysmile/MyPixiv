package org.example.PCOI.ResponseDTO;

import lombok.Data;

@Data
public class R_OverviewContribution {
    private Integer contributionId; // 作品ID
    private String title;          // 作品标题
    private String uploader;       // 上传者用户名
    private String uploaderAvatarPath; // 上传者头像路径
    private String imagePath;      // 作品图片路径
    private Integer viewCount;      // 浏览数
    private Integer favoriteCount;        // 收藏数
    private Integer likeCount;      // 点赞数
    private Integer commentCount;   // 评论数
    private String dismissalReason;   // 驳回理由
}
