package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.*;
import org.example.PCOI.Mapper.*;
import org.example.PCOI.ResponseDTO.*;
import org.example.PCOI.Service.Inter.UserService;
import org.example.PCOI.Service.Support.FileStorageService;
import org.example.PCOI.Service.Support.TransformService;
import org.example.PCOI.Utils.BcryptUtil;
import org.example.PCOI.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.example.PCOI.Service.Support.Enum.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper usermapper;
    @Autowired
    private SecurityIssueMapper securityissuemapper;
    @Autowired
    private ContributionMapper contributionmapper;
    @Autowired
    private CommentMapper commentmapper;
    @Autowired
    private FollowMapper followmapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TransformService transformService;

    @Override
    public boolean register(String username, String password, Integer gender, List<R_SecurityIssue> securityIssues, MultipartFile avatar) {
        if (usermapper.selectUserByName(username) != null) {
            return false; // 用户名已存在
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(BcryptUtil.hash(password));
        user.setSex(gender);
        user.setRole(normalUser);
        user.setStatus(normal);
        usermapper.insertUser(user);
        user = usermapper.selectUserByName(username); // 获取插入后的用户以获取其 ID
        // 仅当上传了头像时才覆盖数据库默认头像 URL
        String avatarUrl = fileStorageService.saveAvatar(avatar, user.getUserId());
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            user.setAvatar(avatarUrl);
            usermapper.updateUser(user);
        }
        for(R_SecurityIssue issue : securityIssues) {
            SecurityIssue securityIssue = transformService.transformRSecurityIssueToSecurityIssue(issue, user.getUserId());
            securityissuemapper.insertSecurityIssue(securityIssue);
        }
        return true;
    }

    @Override
    public R_LoginDTO login(String username, String password) {
        User user = usermapper.selectUserByName(username);
        if(user == null || !BcryptUtil.matches(password, user.getPassword())) {
            return null;
        }
        Claims claims = new Claims(user.getUsername(), user.getUserId(),user.getRole(),login);
        String token = JwtUtil.genToken(claims.toMap());
        R_User rUser = transformService.transformUserToRUser(user);
        R_LoginDTO loginDTO = new R_LoginDTO();
        loginDTO.setUser(rUser);
        loginDTO.setToken(token);
        return loginDTO;
    }

    @Override
    public boolean updatePassword(String tokenUsername, Integer type, String username, String newPassword) {
        if(!tokenUsername.equals(username)||!type.equals(updatePWD)) {
            return false; // 鉴权失败
        }
        User user = usermapper.selectUserByName(username);
        if(user == null) {
            return false; // 用户不存在
        }
        user.setPassword(BcryptUtil.hash(newPassword));
        usermapper.updateUser(user);
        return true;
    }

    @Override
    public List<R_OverviewContribution> getContributionList(String userId) {
        User user = usermapper.selectUserById(userId);
        List<Contribution> contributions = contributionmapper.selectContributionsByAuthorId(userId);
        List<R_OverviewContribution> rOverviewContributions = new ArrayList<>();
        for(Contribution contribution : contributions) {
            R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
            rOverviewContributions.add(rOverviewContribution);
        }
        return rOverviewContributions;
    }

    @Override
    public R_Audit_My_ContributionsDTO getMyContributions(String userId) {
        User user = usermapper.selectUserById(userId);
        List<R_OverviewContribution> pendingContributions = new ArrayList<>();
        List<R_OverviewContribution> approvedContributions = new ArrayList<>();
        List<R_OverviewContribution> dismissalContributions = new ArrayList<>();
        List<Contribution> pendingList = contributionmapper.selectContributionsByAuthorIdAndAuditStatus(userId,pending);
        List<Contribution> approvedList = contributionmapper.selectContributionsByAuthorIdAndAuditStatus(userId,approved);
        List<Contribution> dismissalList = contributionmapper.selectContributionsByAuthorIdAndAuditStatus(userId, dismissal);
        for(Contribution contribution : pendingList) {
            R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
            pendingContributions.add(rOverviewContribution);
        }
        for(Contribution contribution : approvedList) {
            R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
            approvedContributions.add(rOverviewContribution);
        }
        for(Contribution contribution : dismissalList) {
            R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
            dismissalContributions.add(rOverviewContribution);
        }
        R_Audit_My_ContributionsDTO myContributionsDTO = new R_Audit_My_ContributionsDTO();
        myContributionsDTO.setPendingContributions(pendingContributions);
        myContributionsDTO.setApprovedContributions(approvedContributions);
        myContributionsDTO.setDismissalContributions(dismissalContributions);
        return myContributionsDTO;
    }

    @Override
    public List<R_User> getConcernedList(String userId) {
        List<User> concernedUsers = usermapper.selectFollowedUsersByUserId(userId);
        List<R_User> rUsers = new ArrayList<>();
        for(User user : concernedUsers) {
            R_User rUser = transformService.transformUserToRUser(user);
            rUsers.add(rUser);
        }
        return rUsers;
    }

    @Override
    public List<R_OverviewContribution> getLikedList(String userId) {
        List<Contribution> likedContributions = contributionmapper.selectLikeContributionsByUserId(userId);
        List<R_OverviewContribution> rOverviewContributions = new ArrayList<>();
        for(Contribution contribution : likedContributions) {
            User user = usermapper.selectUserById(contribution.getAuthorId());
            R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
            rOverviewContributions.add(rOverviewContribution);
        }
        return rOverviewContributions;
    }

    @Override
    public List<R_OverviewContribution> getFavouriteList(String userId) {
        List<Contribution> favouriteContributions = contributionmapper.selectFavoriteContributionsByUserId(userId);
        List<R_OverviewContribution> rOverviewContributions = new ArrayList<>();
        for(Contribution contribution : favouriteContributions) {
            User user = usermapper.selectUserById(contribution.getAuthorId());
            R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
            rOverviewContributions.add(rOverviewContribution);
        }
        return rOverviewContributions;
    }

    @Override
    public List<R_UserComment> getUserCommentList(String userId) {
        List<R_UserComment> rUserComments = new ArrayList<>();
        List<Comment> comments = commentmapper.selectCommentsByAuthorId(userId);
        for(Comment comment : comments) {
            Contribution contribution = contributionmapper.selectContributionById(comment.getContribution());
            User contributionUser = usermapper.selectUserById(contribution.getAuthorId());
            User commentUser = usermapper.selectUserById(comment.getAuthor());
            R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, contributionUser.getAvatar());
            R_UserComment rUserComment = transformService.transformCommentToRUserComment(comment, rOverviewContribution,commentUser.getAvatar());
            rUserComments.add(rUserComment);
        }
        return rUserComments;
    }

    @Override
    public boolean deleteComment(String commentId, String userId) {
        Comment comment = commentmapper.selectCommentById(commentId);
        if(comment == null || !comment.getAuthor().equals(userId)) {
            return false; // 评论不存在或用户无权限删除
        }
        commentmapper.deleteCommentById(commentId);
        return true;
    }

    @Override
    public boolean deleteContribution(String contributionId, String userId) {
        Contribution contribution = contributionmapper.selectContributionById(contributionId);
        if(contribution == null || !contribution.getAuthorId().equals(userId)) {
            return false; // 作品不存在或用户无权限删除
        }
        contributionmapper.deleteContributionById(contributionId);
        return true;
    }

    @Override
    public R_UserInfoDTO getUserInfo(String requesterId, String userId) {
        User user = usermapper.selectUserById(userId);
        if(user == null) {
            return null; // 用户不存在
        }
        R_User rUser = transformService.transformUserToRUser(user);
        R_UserInfoDTO userInfoDTO = new R_UserInfoDTO();
        userInfoDTO.setUser(rUser);
        boolean isConcerned = followmapper.isFollow(requesterId, userId);
        userInfoDTO.setIsConcerned(isConcerned);
        return userInfoDTO;
    }

    @Override
    public boolean updateUserInfo(String userId, String newUsername, Integer newGender, MultipartFile newAvatar) {
        User user = usermapper.selectUserById(userId);
        if(user == null) {
            return false; // 用户不存在
        }
        if(newUsername!=null && !newUsername.isEmpty()) {
            User existingUser = usermapper.selectUserByName(newUsername);
            if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                return false; // 新用户名已被其他用户使用
            }
            user.setUsername(newUsername);
        }
        if(newGender != null) {
            user.setSex(newGender);
        }
        if(newAvatar != null && !newAvatar.isEmpty()) {
            String avatarUrl = fileStorageService.saveAvatar(newAvatar,userId);
            user.setAvatar(avatarUrl);
        }
        usermapper.updateUser(user);
        return true;
    }

    @Override
    public boolean concernUser(String userId, String concernedUserId) {
        User user = usermapper.selectUserById(userId);
        User concernedUser = usermapper.selectUserById(concernedUserId);
        if(user == null || concernedUser == null) {
            return false; // 用户不存在
        }
        if(followmapper.isFollow(userId, concernedUserId)) {
            return true; // 已关注，幂等处理
        }
        followmapper.insertFollow(userId, concernedUserId);
        return true;
    }

    @Override
    public boolean unconcernUser(String userId, String concernedUserId) {
        User user = usermapper.selectUserById(userId);
        User concernedUser = usermapper.selectUserById(concernedUserId);
        if (user == null || concernedUser == null) {
            return false; // 用户不存在
        }
        if (!followmapper.isFollow(userId, concernedUserId)) {
            return true; // 未关注，幂等处理
        }
        followmapper.deleteFollow(userId, concernedUserId);
        return true;
    }

    @Override
    public List<String> getMySecurityIssues(String username) {
        User user = usermapper.selectUserByName(username);
        if (user == null) {
            return null; // 用户不存在
        }
        List<SecurityIssue> securityIssues = securityissuemapper.selectSecurityIssuesByUserId(user.getUserId());
        List<String> questions = new ArrayList<>();
        for (SecurityIssue issue : securityIssues) {
            questions.add(issue.getDescription());
        }
        return questions;
    }

    @Override
    public R_VerifySecurityIssuesDTO verifySecurityIssues(String username, List<R_SecurityIssue> securityIssues) {
        User user = usermapper.selectUserByName(username);
        if (user == null) {
            return null; // 用户不存在
        }
        List<SecurityIssue> storedIssues = securityissuemapper.selectSecurityIssuesByUserId(user.getUserId());
        if (storedIssues.size() != securityIssues.size()) {
            return null; // 问题数量不匹配
        }
        for (R_SecurityIssue rIssue : securityIssues) {
            boolean matchFound = false;
            for (SecurityIssue storedIssue : storedIssues) {
                if (storedIssue.getDescription().equals(rIssue.getDescription()) &&
                        storedIssue.getAnswer().equals(rIssue.getAnswer())) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                return null; // 有问题不匹配
            }
        }
        String token = JwtUtil.genToken(new Claims(username, user.getUserId(), user.getRole(), updatePWD).toMap());
        R_VerifySecurityIssuesDTO dto = new R_VerifySecurityIssuesDTO();
        dto.setVerified(true);
        dto.setTempToken(token);
        return dto ;
    }
}
