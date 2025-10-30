package org.example.PCOI.Service.Support;

import org.example.PCOI.Entity.Comment;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.User;
import org.example.PCOI.ResponseDTO.*;
import org.springframework.stereotype.Service;

@Service
public class TransformService {
    public R_User transformUserToRUser(User user)
    {
        R_User rUser = new R_User();
        rUser.setUserId(user.getUserId());
        rUser.setRole(user.getRole());
        rUser.setStatus(user.getStatus());
        rUser.setUsername(user.getUsername());
        rUser.setSex(user.getSex());
        rUser.setAvatar(user.getAvatar());
        return rUser;
    }

    public SecurityIssue transformRSecurityIssueToSecurityIssue(R_SecurityIssue rSecurityIssue,String userId)
    {
        SecurityIssue securityIssue = new SecurityIssue();
        securityIssue.setUserId(userId);
        securityIssue.setDescription(rSecurityIssue.getDescription());
        securityIssue.setAnswer(rSecurityIssue.getAnswer());
        return securityIssue;
    }

    public R_OverviewContribution transformContributionToROverviewContribution(Contribution contribution,String avatar)
    {
        R_OverviewContribution rOverviewContribution = new R_OverviewContribution();
        rOverviewContribution.setContributionId(contribution.getContributionId());
        rOverviewContribution.setTitle(contribution.getTitle());
        rOverviewContribution.setAuthorId(contribution.getAuthorId());
        rOverviewContribution.setImage(contribution.getImage());
        rOverviewContribution.setViewCount(contribution.getViewCount());
        rOverviewContribution.setFavoriteCount(contribution.getFavoriteCount());
        rOverviewContribution.setLikeCount(contribution.getLikeCount());
        rOverviewContribution.setCommentCount(contribution.getCommentCount());
        rOverviewContribution.setDismissalReason(contribution.getDismissalReason());
        rOverviewContribution.setAvatar(avatar);
        return rOverviewContribution;
    }

    public R_ContributionComment transformCommentToRContributionComment(Comment comment,String avatar)
    {
        R_ContributionComment rContributionComment = new R_ContributionComment();
        rContributionComment.setAuthor(comment.getAuthor());
        rContributionComment.setDescription(comment.getDescription());
        rContributionComment.setAvatar(avatar);
        rContributionComment.setTime(comment.getTime());
        return rContributionComment;
    }

    public R_UserComment transformCommentToRUserComment(Comment comment, R_OverviewContribution contribution,String avatar)
    {
        R_UserComment rUserComment = new R_UserComment();
        rUserComment.setComment(transformCommentToRContributionComment(comment,avatar));
        rUserComment.setContribution(contribution);
        return rUserComment;
    }
}
