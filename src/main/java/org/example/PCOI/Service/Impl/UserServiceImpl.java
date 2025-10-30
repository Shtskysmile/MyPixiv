package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.User;
import org.example.PCOI.ResponseDTO.*;
import org.example.PCOI.Service.Inter.UserService;
import org.example.PCOI.Service.Support.FileStorageService;
import org.example.PCOI.Utils.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper usermapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public boolean register(String username, String password, String gender, List<R_SecurityIssue> RSecurityIssues, MultipartFile avatar) {
        if (usermapper.selectUserByName(username) != null) {
            return false; // 用户名已存在
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(BcryptUtil.hash(password));
        user.setSex(gender);
        String avatarUrl = fileStorageService.saveAvatar(avatar);
        user.setAvatar(avatarUrl);

        return true;
    }

    @Override
    public R_LoginDTO login(String username, String password) {
        // TODO: 登录逻辑：查询用户、校验密码、签发 JWT、返回必要信息
        return null;
    }

    @Override
    public boolean updatePassword(String tokenUsername, String type, String username, String newPassword) {
        // TODO: 密码更新逻辑：校验临时令牌、强度校验、哈希入库
        return false;
    }

    @Override
    public List<R_OverviewContribution> getContributionList(Integer userId) {
        // TODO: 查询用户作品列表
        return Collections.emptyList();
    }

    @Override
    public R_Audit_My_ContributionsDTO getMyContributions(Integer userId) {
        // TODO: 查询我的作品（含审核状态）
        return null;
    }

    @Override
    public List<R_User> getConcernedList(Integer userId) {
        // TODO: 查询关注列表
        return Collections.emptyList();
    }

    @Override
    public List<R_OverviewContribution> getLikedList(Integer userId) {
        // TODO: 查询点赞列表
        return Collections.emptyList();
    }

    @Override
    public List<R_OverviewContribution> getFavouriteList(Integer userId) {
        // TODO: 查询收藏列表
        return Collections.emptyList();
    }

    @Override
    public List<R_UserComment> getUserCommentList(Integer userId) {
        // TODO: 查询我的评论
        return Collections.emptyList();
    }

    @Override
    public boolean deleteComment(Integer commentId, Integer userId) {
        // TODO: 删除评论（需鉴权）
        return false;
    }

    @Override
    public boolean deleteContribution(Integer contributionId, Integer userId) {
        // TODO: 删除作品（需鉴权与状态判断）
        return false;
    }

    @Override
    public R_UserInfoDTO getUserInfo(Integer requesterId, Integer userId) {
        // TODO: 查询用户资料（按请求者身份控制可见字段）
        return null;
    }

    @Override
    public boolean updateUserInfo(Integer userId, String newUsername, String newGender, MultipartFile newAvatar) {
        // TODO: 更新用户资料（唯一性与文件处理）
        return false;
    }

    @Override
    public boolean concernUser(Integer userId, Integer concernedUserId) {
        // TODO: 关注用户（幂等处理）
        return false;
    }

    @Override
    public boolean unconcernUser(Integer userId, Integer concernedUserId) {
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
