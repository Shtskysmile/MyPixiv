package org.example.PCOI.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Contribution {
    private String contributionId;              // 作品ID
    private Integer type;            // 作品类型0-插画，1-漫画
    private String title;         // 作品标题
    private String image;         // 作品图片路径
    private String description;  // 描述信息
    private Integer status;        // 状态(正常状态,封禁状态）0-正常，1-封禁
    private Integer auditStatus;   // 审核状态（待审核，已通过，已驳回）0-待审核，1-已通过，2-已驳回
    private LocalDateTime publishTime;         // 上传时间
    private String authorId;     // 上传者
    private Integer viewCount;      // 浏览数
    private Integer favoriteCount;        // 收藏数
    private Integer likeCount;      // 点赞数
    private Integer commentCount;   // 评论数
    private String dismissalReason; // 驳回理由
}
