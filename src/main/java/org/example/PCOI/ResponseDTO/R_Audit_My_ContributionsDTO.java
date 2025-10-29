package org.example.PCOI.ResponseDTO;

import lombok.Data;

@Data
public class R_Audit_My_ContributionsDTO {
    private R_OverviewContribution[] unAuditContributions; // 待审核作品列表
    private R_OverviewContribution[] approvedContributions; // 审核通过作品列表
    private R_OverviewContribution[] dismissalContributions; // 审核未通过作品列表

}
