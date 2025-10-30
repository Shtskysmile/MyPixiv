package org.example.PCOI.Service.Inter;

import org.example.PCOI.ResponseDTO.R_Audit_My_ContributionsDTO;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.example.PCOI.ResponseDTO.R_User;

import java.util.List;
import java.util.Map;

public interface CommunityAdminService {
    boolean blockUser(Integer userId);
    boolean unblockUser(Integer userId);
    boolean blockContribution(Integer contributionId);
    boolean unblockContribution(Integer contributionId);
    R_Audit_My_ContributionsDTO auditContributions();
    boolean dismissContribution(Integer contributionId, String dismissalReason);
    boolean approveContribution(Integer contributionId);
    List<R_User> getBlockedUsers();
    List<R_OverviewContribution> getBlockedContributions();
    boolean deleteComment(Integer commentId);
}

