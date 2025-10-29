package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 账号相关
    boolean register(String username, String password, String gender, List<SecurityIssue> securityIssues, MultipartFile avatar);
    Map<String,Object> login(String username, String password);
    void updatePassword(String username, String newPassword);

    // 作品和用户信息相关
    List<OverviewContribution> getContributionList(Integer userId);
    Map<String, Object> getMyContributions(Integer userId);
    List<User> getConcernedList(Integer userId);
    List<OverviewContribution> getLikedList(Integer userId);
    List<OverviewContribution> getFavouriteList(Integer userId);
    List<UserComment> getUserCommentList(Integer userId);
    boolean deleteComment(Integer commentId, Integer userId);
    boolean deleteContribution(Integer contributionId, Integer userId);
    Map<String, Object> getUserInfo(Integer userId);
    boolean updateUserInfo(Integer userId, String newUsername, String newGender, MultipartFile newAvatar);

    // 关注相关
    boolean concernUser(Integer userId, Integer concernedUserId);
    boolean unconcernUser(Integer userId, Integer concernedUserId);

    // 密保相关
    List<String> getMySecurityIssues(String username);
    boolean verifySecurityIssues(String username, List<SecurityIssue> securityIssues);
}
