package org.example.PCOI.Service.Inter;

import org.example.PCOI.ResponseDTO.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 账号相关
    boolean register(String username, String password, String gender, List<R_SecurityIssue> RSecurityIssues, MultipartFile avatar);
    R_LoginDTO login(String username, String password);
    boolean updatePassword(String tokenUsername,String type,String username, String newPassword);

    // 作品和用户信息相关
    List<R_OverviewContribution> getContributionList(Integer userId);
    R_Audit_My_ContributionsDTO getMyContributions(Integer userId);
    List<R_User> getConcernedList(Integer userId);
    List<R_OverviewContribution> getLikedList(Integer userId);
    List<R_OverviewContribution> getFavouriteList(Integer userId);
    List<R_UserComment> getUserCommentList(Integer userId);
    boolean deleteComment(Integer commentId, Integer userId);
    boolean deleteContribution(Integer contributionId, Integer userId);
    R_UserInfoDTO getUserInfo(Integer requesterId,Integer userId);
    boolean updateUserInfo(Integer userId, String newUsername, String newGender, MultipartFile newAvatar);

    // 关注相关
    boolean concernUser(Integer userId, Integer concernedUserId);
    boolean unconcernUser(Integer userId, Integer concernedUserId);

    // 密保相关
    List<String> getMySecurityIssues(String username);
    R_VerifySecurityIssuesDTO verifySecurityIssues(String username, List<R_SecurityIssue> RSecurityIssues);
}
