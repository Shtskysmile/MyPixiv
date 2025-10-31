package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.Comment;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.CommentMapper;
import org.example.PCOI.Mapper.ContributionMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.ResponseDTO.R_Audit_My_ContributionsDTO;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.example.PCOI.ResponseDTO.R_User;
import org.example.PCOI.Service.Inter.CommunityAdminService;
import org.example.PCOI.Service.Support.TransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

import static org.example.PCOI.Service.Support.Enum.*;

@Slf4j
@Service
public class CommunityAdminServiceImpl implements CommunityAdminService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ContributionMapper contributionMapper;
    @Autowired
    private TransformService transformService;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public boolean blockUser(String userId) {
        try{
            User user = userMapper.selectUserById(userId);
            if(user==null)
                return false;
            user.setStatus(banned);
            userMapper.updateUser(user);
            return true;
        }catch (Exception e){
            log.error("Error block user，userId：{}:{}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unblockUser(String userId) {
        try{
            User user = userMapper.selectUserById(userId);
            if(user==null)
                return false;
            user.setStatus(normal);
            userMapper.updateUser(user);
            return true;
        }catch (Exception e){
            log.error("Error unblock user，userId：{}:{}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean blockContribution(String contributionId) {
        try{
            Contribution contribution = contributionMapper.selectContributionById(contributionId);
            if(contribution==null)
                return false;
            contribution.setStatus(banned);
            contributionMapper.updateContribution(contribution);
            return true;
        }catch (Exception e){
            log.error("Error block contribution，contributionId：{}:{}", contributionId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unblockContribution(String contributionId) {
        try{
            Contribution contribution = contributionMapper.selectContributionById(contributionId);
            if(contribution==null)
                return false;
            contribution.setStatus(normal);
            contributionMapper.updateContribution(contribution);
            return true;
        }catch (Exception e){
            log.error("Error unblock contribution，contributionId：{}:{}", contributionId, e.getMessage());
            return true;
        }
    }

    @Override
    public R_Audit_My_ContributionsDTO auditContributions() {
        try{
            List<R_OverviewContribution> pendingContributions = new ArrayList<>();
            List<R_OverviewContribution> approvedContributions = new ArrayList<>();
            List<R_OverviewContribution> dismissalContributions = new ArrayList<>();
            List<Contribution> pendingList = contributionMapper.selectContributionsByAuditStatus(pending);
            List<Contribution> approvedList = contributionMapper.selectContributionsByAuditStatus(approved);
            List<Contribution> dismissalList = contributionMapper.selectContributionsByAuditStatus(dismissal);
            for(Contribution contribution : pendingList){
                User user = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution,user.getAvatar());
                pendingContributions.add(rOverviewContribution);
            }
            for(Contribution contribution : approvedList){
                User user = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution,user.getAvatar());
                approvedContributions.add(rOverviewContribution);
            }
            for(Contribution contribution : dismissalList){
                User user = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution,user.getAvatar());
                dismissalContributions.add(rOverviewContribution);
            }
            R_Audit_My_ContributionsDTO rAuditContributionsDTO = new R_Audit_My_ContributionsDTO();
            rAuditContributionsDTO.setPendingContributions(pendingContributions);
            rAuditContributionsDTO.setApprovedContributions(approvedContributions);
            rAuditContributionsDTO.setDismissalContributions(dismissalContributions);
            return rAuditContributionsDTO;
        }catch (Exception e){
            log.error("Error audit contributions:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean dismissContribution(String contributionId, String dismissalReason) {
        try {
            Contribution contribution = contributionMapper.selectContributionById(contributionId);
            if (contribution == null)
                return false;
            contribution.setStatus(dismissal);
            contribution.setDismissalReason(dismissalReason);
            contributionMapper.updateContribution(contribution);
            return true;
        }catch (Exception e){
            log.error(("Error dismiss contribution，contributionId：{}:{}"),contributionId,e.getMessage());
            return false;
        }
    }

    @Override
    public boolean approveContribution(String contributionId) {
        try {
            Contribution contribution = contributionMapper.selectContributionById(contributionId);
            if (contribution == null)
                return false;
            contribution.setStatus(approved);
            contribution.setDismissalReason(null);
            contributionMapper.updateContribution(contribution);
            return true;
        }catch (Exception e){
            log.error(("Error approve contribution，contributionId：{}:{}"),contributionId,e.getMessage());
            return false;
        }
    }

    @Override
    public List<R_User> getBlockedUsers() {
        try{
            List<R_User> blockedRUsers = new ArrayList<>();
            List<User> blockedUsers = userMapper.selectUsersByStatus(banned);
            for(User user : blockedUsers){
                R_User rUser = transformService.transformUserToRUser(user);
                blockedRUsers.add(rUser);
            }
            return blockedRUsers;
        }catch (Exception e){
            log.error("Error get blocked users:{}",e.getMessage());
            return null;
        }
    }

    @Override
    public List<R_OverviewContribution> getBlockedContributions() {
        try{
            List<R_OverviewContribution> blockedRContributions = new ArrayList<>();
            List<Contribution> blockedContributions = contributionMapper.selectContributionsByStatus(banned);
            for(Contribution contribution : blockedContributions){
                User user = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution,user.getAvatar());
                blockedRContributions.add(rOverviewContribution);
            }
            return blockedRContributions;
        }catch( Exception e){
            log.error("Error get blocked contributions:{}",e.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteComment(String commentId) {
        try{
            Comment comment = commentMapper.selectCommentById(commentId);
            if(comment==null)
                return false;
            commentMapper.deleteCommentById(commentId);
            return true;
        }catch (Exception e){
            log.error("Error delete comment，commentId：{}:{}", commentId, e.getMessage());
            return false;
        }
    }
}

