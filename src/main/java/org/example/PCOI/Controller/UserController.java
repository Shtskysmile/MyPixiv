package org.example.PCOI.Controller;

import org.example.PCOI.Entity.*;
import org.example.PCOI.Service.Inter.LogService;
import org.example.PCOI.Service.Inter.UserService;
import org.example.PCOI.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController()
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LogService logService;

    @PostMapping("/register")
    public Result<String> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("gender") String gender,
            @RequestParam("securityIssues") List<SecurityIssue> securityIssues,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar){
        try {
            boolean success = userService.register(username, password, gender, securityIssues, avatar);
            if (success) {
                logService.logMethodExecution("user:register");
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
    public Result<Map<String, Object>> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        try {
            User user = userService.login(username, password);
            if (user != null) {
                Map<String, Object> claims = Map.of(
                        "userId", user.getUserId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                );
                String token = JwtUtil.genToken(claims);
                Map<String, Object> result = Map.of(
                        "user", user,
                        "token", token
                );
                logService.logMethodExecution("user:login");
                return Result.success(result);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.error("登录过程中出现错误: " + e.getMessage());
        }
    }

    @PostMapping("/contributionList")
    public Result<List<OverviewContribution>> getContributionList(
            @RequestParam ("userId") String userId){
        try {
            List<OverviewContribution> list = userService.getContributionList(Integer.valueOf(userId));
            logService.logMethodExecution("user:contributionList");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取作品列表出错: " + e.getMessage());
        }
    }

    @PostMapping("/myContributions")
    public Result<Map<String,Object>> getMyContributions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("userId") String userId){
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                JwtUtil.parseToken(token);
            }
            Map<String, Object> data = userService.getMyContributions(Integer.valueOf(userId));
            logService.logMethodExecution("user:myContributions");
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取我的作品出错: " + e.getMessage());
        }
    }
    @PostMapping("/concernedList")
    public Result<List<User>> getConcernedList(
            @RequestParam ("userId") String userId){
        try {
            List<User> list = userService.getConcernedList(Integer.valueOf(userId));
            logService.logMethodExecution("user:concernedList");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取关注列表出错: " + e.getMessage());
        }
    }

    @PostMapping("/likedList")
    public Result<List<Contribution>> getLikedList(
            @RequestParam ("userId") String userId){
        try {
            List<Contribution> list = userService.getLikedList(Integer.valueOf(userId));
            logService.logMethodExecution("user:likedList");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取点赞列表出错: " + e.getMessage());
        }
    }


    @PostMapping("/favouriteList")
    public Result<List<Contribution>> getFavouriteList(
            @RequestParam ("userId") String userId){
        try {
            List<Contribution> list = userService.getFavouriteList(Integer.valueOf(userId));
            logService.logMethodExecution("user:favouriteList");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取收藏列表出错: " + e.getMessage());
        }

    }

    @PostMapping("/userCommentList")
    public Result<List<UserComment>> getUserCommentList(
            @RequestParam ("userId") String userId){
        try {
            List<UserComment> list = userService.getUserCommentList(Integer.valueOf(userId));
            logService.logMethodExecution("user:userCommentList");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取评论列表出错: " + e.getMessage());
        }

    }

    @PostMapping("/user/deleteComment")
    public Result<String> deleteComment(
            @RequestParam ("commentId") Integer commentId,
            @RequestParam ("userId") Integer userId){
        try {
            boolean ok = userService.deleteComment(commentId, userId);
            if (ok) {
                logService.logMethodExecution("user:deleteComment");
                return Result.success("删除评论成功");
            }
            return Result.error("删除评论失败");
        } catch (Exception e) {
            return Result.error("删除评论出错: " + e.getMessage());
        }

    }

    @PostMapping("/user/deleteContribution")
    public Result<String> deleteContribution(
            @RequestParam ("contributionId") Integer contributionId,
            @RequestParam ("userId") Integer userId){
        try {
            boolean ok = userService.deleteContribution(contributionId, userId);
            if (ok) {
                logService.logMethodExecution("user:deleteContribution");
                return Result.success("删除作品成功");
            }
            return Result.error("删除作品失败");
        } catch (Exception e) {
            return Result.error("删除作品出错: " + e.getMessage());
        }

    }


    @PostMapping("/userInfo")
    public Result<Map<String,Object>> getUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("userId") String userId) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                JwtUtil.parseToken(token);
            }
            Map<String, Object> data = userService.getUserInfo(Integer.valueOf(userId));
            logService.logMethodExecution("user:userInfo");
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取用户信息出错: " + e.getMessage());
        }
    }

    @PostMapping("/mySecurityIssues")
    public Result<List<String>> getMySecurityIssues(
            @RequestParam ("username") String username){
        try {
            List<String> issues = userService.getMySecurityIssues(username);
            logService.logMethodExecution("user:mySecurityIssues");
            return Result.success(issues);
        } catch (Exception e) {
            return Result.error("获取密保问题出错: " + e.getMessage());
        }

    }

    @PostMapping("/verifySecurityIssues")
    public Result<Map<String,Object>> verifySecurityIssues(
            @RequestParam ("username") String username,
            @RequestParam ("securityIssues") List<SecurityIssue> securityIssues){
        try {
            boolean ok = userService.verifySecurityIssues(username, securityIssues);
            if (!ok) {
                return Result.error("密保校验失败");
            }
            Map<String, Object> claims = Map.of(
                    "username", username,
                    "type", "resetPwd"
            );
            String tempToken = JwtUtil.genToken(claims);
            logService.logMethodExecution("user:verifySecurityIssues");
            return Result.success(Map.of("tempToken", tempToken));
        } catch (Exception e) {
            return Result.error("校验密保出错: " + e.getMessage());
        }

    }

    @PostMapping("/updatePassword")
    public Result<String> updatePassword(
            @RequestHeader("tempToken") String tempToken,
            @RequestParam("username") String username,
            @RequestParam("newPassword") String newPassword) {
        try {
            Map<String, Object> claims = JwtUtil.parseToken(tempToken);
            if (!"resetPwd".equals(claims.get("type")) || !username.equals(claims.get("username"))) {
                return Result.error("无效的操作");
            }
            // 修改密码逻辑
            userService.updatePassword(username, newPassword);
            logService.logMethodExecution("user:updatePassword");
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error("临时令牌无效或已过期");
        }
    }

    @PostMapping("/user/updateUserInfo")
    public Result<String> updateUserInfo(
            @RequestParam ("userId") Integer userId,
            @RequestParam ("newUsername") String newUsername,
            @RequestParam("newGender") String newGender,
            @RequestParam(value = "newAvatar", required = false) MultipartFile newAvatar){
        try {
            boolean ok = userService.updateUserInfo(userId, newUsername, newGender, newAvatar);
            if (ok) {
                logService.logMethodExecution("user:updateUserInfo");
                return Result.success("更新成功");
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新用户信息出错: " + e.getMessage());
        }

    }

    @PostMapping("/user/concernUser")
    public Result<String> concernUser(
            @RequestParam ("userId") Integer userId,
            @RequestParam ("concernedUserId") Integer concernedUserId){
        try {
            boolean ok = userService.concernUser(userId, concernedUserId);
            if (ok) {
                logService.logMethodExecution("user:concernUser");
                return Result.success("关注成功");
            }
            return Result.error("关注失败");
        } catch (Exception e) {
            return Result.error("关注出错: " + e.getMessage());
        }

    }

    @PostMapping("/user/unconcernUser")
    public Result<String> unconcernUser(
            @RequestParam ("userId") Integer userId,
            @RequestParam ("concernedUserId") Integer concernedUserId){
        try {
            boolean ok = userService.unconcernUser(userId, concernedUserId);
            if (ok) {
                logService.logMethodExecution("user:unconcernUser");
                return Result.success("已取消关注");
            }
            return Result.error("取消关注失败");
        } catch (Exception e) {
            return Result.error("取消关注出错: " + e.getMessage());
        }

    }

}
