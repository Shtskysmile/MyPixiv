package org.example.PCOI.Service.Impl;

import org.example.PCOI.ResponseDTO.R_Audit_My_ContributionsDTO;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.example.PCOI.ResponseDTO.R_User;
import org.example.PCOI.Service.Inter.CommunityAdminService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CommunityAdminServiceImpl implements CommunityAdminService {
    @Override
    public boolean blockUser(Integer userId) {
        // TODO: 封禁用户
        return false;
    }

    @Override
    public boolean unblockUser(Integer userId) {
        // TODO: 解除封禁
        return false;
    }

    @Override
    public boolean blockContribution(Integer contributionId) {
        // TODO: 封禁作品
        return false;
    }

    @Override
    public boolean unblockContribution(Integer contributionId) {
        // TODO: 解除作品封禁
        return false;
    }

    @Override
    public R_Audit_My_ContributionsDTO auditContributions() {
        // TODO: 拉取待审核作品
        return null;
    }

    @Override
    public boolean dismissContribution(Integer contributionId, String dismissalReason) {
        // TODO: 驳回作品
        return false;
    }

    @Override
    public boolean approveContribution(Integer contributionId) {
        // TODO: 审核通过
        return false;
    }

    @Override
    public List<R_User> getBlockedUsers() {
        // TODO: 查询被封禁用户
        return Collections.emptyList();
    }

    @Override
    public List<R_OverviewContribution> getBlockedContributions() {
        // TODO: 查询被封禁作品
        return Collections.emptyList();
    }

    @Override
    public boolean deleteComment(Integer commentId) {
        // TODO: 管理员删除评论
        return false;
    }
}

