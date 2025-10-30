package org.example.PCOI.ResponseDTO;

import lombok.Data;

@Data
public class R_UserComment {
    private R_ContributionComment comment; // 评论内容
    private R_OverviewContribution ROverviewContribution; // 作品概览
}
