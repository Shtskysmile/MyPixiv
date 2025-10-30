package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.*;
import org.example.PCOI.Mapper.CommentMapper;
import org.example.PCOI.Mapper.ContributionMapper;
import org.example.PCOI.Mapper.SecurityIssueMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.ResponseDTO.*;
import org.example.PCOI.Service.Inter.UserService;
import org.example.PCOI.Service.Support.FileStorageService;
import org.example.PCOI.Service.Support.TransformService;
import org.example.PCOI.Utils.BcryptUtil;
import org.example.PCOI.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

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
    private FileStorageService fileStorageService;

    @Autowired
    private TransformService transformService;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public boolean register(String username, String password, String gender, List<R_SecurityIssue> SecurityIssues, MultipartFile avatar) {
        if (usermapper.selectUserByName(username) != null) {
            return false; // 用户名已存在
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(BcryptUtil.hash(password));
        user.setSex(gender);
        String avatarUrl = fileStorageService.saveAvatar(avatar);
        user.setAvatar(avatarUrl);
        usermapper.insertUser(user);
        user = usermapper.selectUserByName(username); // 获取插入后的用户以获取其 ID
        for(R_SecurityIssue issue : SecurityIssues) {
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
        Claims claims = new Claims(user.getUsername(), user.getUserId(),user.getRole(),"login" );
        String token = JwtUtil.genToken(claims.toMap());
        R_User rUser = transformService.transformUserToRUser(user);
        R_LoginDTO loginDTO = new R_LoginDTO();
        loginDTO.setUser(rUser);
        loginDTO.setToken(token);
        return loginDTO;
    }

    @Override
    public boolean updatePassword(String tokenUsername, String type, String username, String newPassword) {
        if(!tokenUsername.equals(username)||!type.equals("updatePWD")) {
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
        try{
            User user = usermapper.selectUserById(userId);
            List<Contribution> contributions = usermapper.selectApprovedContributionsByUserId(userId);
            List<R_OverviewContribution> rOverviewContributions = null;
            for(Contribution contribution : contributions) {
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
                rOverviewContributions.add(rOverviewContribution);
            }
            return rOverviewContributions;
        }catch (Exception e){
            log.error("Error fetching contributions for userId {}: {}", userId, e.getMessage());
            return null;
        }

    }

    @Override
    public R_Audit_My_ContributionsDTO getMyContributions(String userId) {
        try{
            User user = usermapper.selectUserById(userId);
            List<R_OverviewContribution> pendingContributions = null;
            List<R_OverviewContribution> approvedContributions = null;
            List<R_OverviewContribution> dismissalContributions = null;
            List<Contribution> pending = usermapper.selectPendingContributionsByUserId(userId);
            List<Contribution> approved = usermapper.selectApprovedContributionsByUserId(userId);
            List<Contribution> dismissal = usermapper.selectDismissalContributionsByUserId(userId);
            for(Contribution contribution : pending) {
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
                pendingContributions.add(rOverviewContribution);
            }
            for(Contribution contribution : approved) {
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
                approvedContributions.add(rOverviewContribution);
            }
            for(Contribution contribution : dismissal) {
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
                dismissalContributions.add(rOverviewContribution);
            }
            R_Audit_My_ContributionsDTO myContributionsDTO = new R_Audit_My_ContributionsDTO();
            myContributionsDTO.setPendingContributions(pendingContributions);
            myContributionsDTO.setApprovedContributions(approvedContributions);
            myContributionsDTO.setDismissalContributions(dismissalContributions);
            return myContributionsDTO;
        }catch(Exception e){
            log.error("Error fetching my contributions for userId {}: {}", userId, e.getMessage());
            return null;
        }

    }

    @Override
    public List<R_User> getConcernedList(String userId) {
        try{
            List<User> concernedUsers = usermapper.selectConcernedUsersByUserId(userId);
            List<R_User> rUsers = null;
            for(User user : concernedUsers) {
                R_User rUser = transformService.transformUserToRUser(user);
                rUsers.add(rUser);
            }
            return rUsers;
        }catch(Exception e){
            log.error("Error fetching concerned users for userId {}: {}", userId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<R_OverviewContribution> getLikedList(String userId) {
        try{
            List<Contribution> likedContributions = contributionmapper.selectLikeContributionsByUserId(userId);
            List<R_OverviewContribution> rOverviewContributions = null;
            for(Contribution contribution : likedContributions) {
                User user = usermapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
                rOverviewContributions.add(rOverviewContribution);
            }
            return rOverviewContributions;
        }catch (Exception e){
            log.error("Error fetching liked contributions for userId {}: {}", userId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<R_OverviewContribution> getFavouriteList(String userId) {
        try{
            List<Contribution> favouriteContributions = contributionmapper.selectFavoriteContributionsByUserId(userId);
            List<R_OverviewContribution> rOverviewContributions = null;
            for(Contribution contribution : favouriteContributions) {
                User user = usermapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
                rOverviewContributions.add(rOverviewContribution);
            }
            return rOverviewContributions;
        }catch (Exception e){
            log.error("Error fetching favourite contributions for userId {}: {}", userId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<R_UserComment> getUserCommentList(String userId) {
        try{
            List<R_UserComment> rUserComments = null;
            List<Comment> comments = commentMapper.selectCommentsByUserId(userId);
            for(Comment comment : comments) {
                Contribution contribution = contributionmapper.selectContributionById(comment.getContribution());
                User contribtinUser = usermapper.selectUserById(contribution.getAuthorId());
                User commentUser = usermapper.selectUserById(comment.getAuthor());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, contribtinUser.getAvatar());
                R_UserComment rUserComment = transformService.transformCommentToRUserComment(comment, rOverviewContribution);
                rUserComments.add(rUserComment);
            }
            return rUserComments;
        }catch (Exception e){
            log.error("Error fetching user comments for userId {}: {}", userId, e.getMessage());
            return null;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean deleteComment(String commentId, String userId) {
        // TODO: 删除评论（需鉴权）
        return false;
    }

    @Override
    public boolean deleteContribution(String contributionId, String userId) {
        // TODO: 删除作品（需鉴权与状态判断）
        return false;
    }

    @Override
    public R_UserInfoDTO getUserInfo(String requesterId, String userId) {
        // TODO: 查询用户资料（按请求者身份控制可见字段）
        return null;
    }

    @Override
    public boolean updateUserInfo(String userId, String newUsername, String newGender, MultipartFile newAvatar) {
        // TODO: 更新用户资料（唯一性与文件处理）
        return false;
    }

    @Override
    public boolean concernUser(String userId, String concernedUserId) {
        // TODO: 关注用户（幂等处理）
        return false;
    }

    @Override
    public boolean unconcernUser(String userId, String concernedUserId) {
        // TODO: 取消关注（幂等处理）
        return false;
    }

    @Override
    public List<String> getMySecurityIssues(String username) {
        // TODO: 读取用户密保问题
        return Collections.emptyList();
    }

    @Override
    public R_VerifySecurityIssuesDTO verifySecurityIssues(String username, List<R_SecurityIssue> RSecurityIssues) {
        // TODO: 校验密保问题与答案
        return new R_VerifySecurityIssuesDTO(false, null);
    }
}
