package org.example.PCOI.Entity;

import lombok.Data;

@Data
public class UserComment {
    private ContributionComment comment; // 评论内容
    private OverviewContribution overviewContribution; // 作品概览
}
