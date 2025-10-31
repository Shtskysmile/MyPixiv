package org.example.PCOI.Controller;
import org.example.PCOI.ResponseDTO.*;
import org.example.PCOI.Service.Inter.UserService;
import org.example.PCOI.Utils.TokenProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController()
public class UserController {
    @Autowired
    private UserService userService;
    

    @PostMapping("/register")
    public Result<String> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("gender") Integer gender,
            @RequestParam("RSecurityIssues") List<R_SecurityIssue> SecurityIssues,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar){
        try {
            boolean success = userService.register(username, password, gender, SecurityIssues, avatar);
            if (success) {
                return Result.success("注册成功");
            } else {
                return Result.error("注册失败，用户名已存在");
            }
        }
        catch (Exception e) {
                return Result.error("注册过程中出现错误: " + e.getMessage());
            }


    }

    @PostMapping("/login")
    public Result<R_LoginDTO> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        try {
            R_LoginDTO result = userService.login(username, password);
            if (result != null) {
                System.out.println("生成的Token: " + result.getToken());
                return Result.success(result);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.error("登录过程中出现错误: " + e.getMessage());
        }
    }

    @PostMapping("/contributionList")
    public Result<List<R_OverviewContribution>> getContributionList(
            @RequestParam ("userId") String userId){
        try {
            List<R_OverviewContribution> list = userService.getContributionList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取作品列表出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/myContributions")
    public Result<R_Audit_My_ContributionsDTO> getMyContributions(
            @RequestHeader("Authorization") String authHeader){
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            R_Audit_My_ContributionsDTO data = userService.getMyContributions(userId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取我的作品出错: " + e.getMessage());
        }
    }
    @PostMapping("/concernedList")
    public Result<List<R_User>> getConcernedList(
            @RequestParam ("userId") String userId){
        try {
            List<R_User> list = userService.getConcernedList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取关注列表出错: " + e.getMessage());
        }
    }

    @PostMapping("/likedList")
    public Result<List<R_OverviewContribution>> getLikedList(
            @RequestParam ("userId") String userId){
        try {
            List<R_OverviewContribution> list = userService.getLikedList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取点赞列表出错: " + e.getMessage());
        }
    }


    @PostMapping("/favouriteList")
    public Result<List<R_OverviewContribution>> getFavouriteList(
            @RequestParam ("userId") String userId){
        try {
            List<R_OverviewContribution> list = userService.getFavouriteList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取收藏列表出错: " + e.getMessage());
        }

    }

    @PostMapping("/userCommentList")
    public Result<List<R_UserComment>> getUserCommentList(
            @RequestParam ("userId") String userId){
        try {
            List<R_UserComment> list = userService.getUserCommentList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取评论列表出错: " + e.getMessage());
        }

    }

    @PostMapping("/user/deleteComment")
    public Result<String> deleteComment(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("commentId") String commentId){
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = userService.deleteComment(commentId, userId);
            if (ok) {
                return Result.success("删除评论成功");
            }
            return Result.error("删除评论失败");
        } catch (Exception e) {
            return Result.error("删除评论出错: " + e.getMessage());
        }

    }

    @PostMapping("/user/deleteContribution")
    public Result<String> deleteContribution(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("contributionId") String contributionId){
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = userService.deleteContribution(contributionId, userId);
            if (ok) {
                return Result.success("删除作品成功");
            }
            return Result.error("删除作品失败");
        } catch (Exception e) {
            return Result.error("删除作品出错: " + e.getMessage());
        }

    }

    @PostMapping("/userInfo")
    public Result<R_UserInfoDTO> getUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("userId") String userId) {
        try {
            String requesterId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            R_UserInfoDTO data = userService.getUserInfo(requesterId,userId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取用户信息出错: " + e.getMessage());
        }
    }

    @PostMapping("/mySecurityIssues")
    public Result<List<String>> getMySecurityIssues(
            @RequestParam("username") String username){
        try {
            List<String> issues = userService.getMySecurityIssues(username);
            return Result.success(issues);
        } catch (Exception e) {
            return Result.error("获取密保问题出错: " + e.getMessage());
        }

    }

    @PostMapping("/verifySecurityIssues")
    public Result<R_VerifySecurityIssuesDTO> verifySecurityIssues(
            @RequestParam("username") String username,
            @RequestParam ("SecurityIssues") List<R_SecurityIssue> SecurityIssues){
        try {
            R_VerifySecurityIssuesDTO data = userService.verifySecurityIssues(username, SecurityIssues);
            if(data.getVerified())
            {
                return Result.success(data);
            }
            return Result.error("密保验证失败");
        } catch (Exception e) {
            return Result.error("校验密保出错: " + e.getMessage());
        }

    }

    @PostMapping("/updatePassword")
    public Result<String> updatePassword(
            @RequestHeader("Authorization") String tempToken,
            @RequestParam("username") String username,
            @RequestParam("newPassword") String newPassword) {
        try {
            String tokenUsername = (String) TokenProcess.getAttributeFromToken(tempToken, "username");
            Integer type = (Integer) TokenProcess.getAttributeFromToken(tempToken, "type");
            boolean ok = userService.updatePassword(tokenUsername,type,username,newPassword);
            if (!ok) {
                return Result.error("密码修改失败");
            }
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error("临时令牌无效或已过期");
        }
    }

    @PostMapping("/user/updateUserInfo")
    public Result<String> updateUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("newUsername") String newUsername,
            @RequestParam("newGender") Integer newGender,
            @RequestParam(value = "newAvatar", required = false) MultipartFile newAvatar){
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = userService.updateUserInfo(userId, newUsername, newGender, newAvatar);
            if (ok) {
                return Result.success("更新成功");
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新用户信息出错: " + e.getMessage());
        }

    }

    @PostMapping("/user/concernUser")
    public Result<String> concernUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("concernedUserId") String concernedUserId){
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = userService.concernUser(userId, concernedUserId);
            if (ok) {
                return Result.success("关注成功");
            }
            return Result.error("关注失败");
        } catch (Exception e) {
            return Result.error("关注出错: " + e.getMessage());
        }

    }

    @PostMapping("/user/unconcernUser")
    public Result<String> unconcernUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("concernedUserId") String concernedUserId){
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = userService.unconcernUser(userId, concernedUserId);
            if (ok) {
                return Result.success("已取消关注");
            }
            return Result.error("取消关注失败");
        } catch (Exception e) {
            return Result.error("取消关注出错: " + e.getMessage());
        }

    }

}
