package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.OverviewContribution;
import org.example.PCOI.Entity.User;

import java.util.List;
import java.util.Map;

public interface CommunityAdminService {
    boolean blockUser(Integer userId);
    boolean unblockUser(Integer userId);
    boolean blockContribution(Integer contributionId);
    boolean unblockContribution(Integer contributionId);
    Map<String, Object> auditContributions();
    boolean dismissContribution(Integer contributionId, String dismissReason);
    boolean approveContribution(Integer contributionId);
    List<User> getBlockedUsers();
    List<OverviewContribution> getBlockedContributions();
    boolean deleteComment(Integer commentId);
}

