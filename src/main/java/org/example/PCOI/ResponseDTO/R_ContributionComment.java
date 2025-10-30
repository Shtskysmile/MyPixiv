package org.example.PCOI.ResponseDTO;
import lombok.Data;

@Data
public class R_ContributionComment {
    private String author;     // 评论用户
    private String description;      // 评论内容
    private String time;    // 评论时间戳
    private String avatar; // 用户头像路径
}
