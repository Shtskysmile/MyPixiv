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
            @RequestPart(name = "SecurityIssues", required = false) List<R_SecurityIssue> SecurityIssues,
            @RequestPart(name = "avatar", required = false) MultipartFile avatar){
        boolean success = userService.register(username, password, gender, SecurityIssues, avatar);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败，用户名已存在");
        }
    }

    @PostMapping("/login")
    public Result<R_LoginDTO> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        R_LoginDTO result = userService.login(username, password);
        if (result != null) {
            return Result.success(result);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    @PostMapping("/contributionList")
    public Result<List<R_OverviewContribution>> getContributionList(
            @RequestParam ("userId") String userId){
        List<R_OverviewContribution> list = userService.getContributionList(userId);
        return Result.success(list);
    }

    @PostMapping("/user/myContributions")
    public Result<R_Audit_My_ContributionsDTO> getMyContributions(
            @RequestHeader("Authorization") String authHeader) throws Exception {
        String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
        R_Audit_My_ContributionsDTO data = userService.getMyContributions(userId);
        return Result.success(data);
    }

    @PostMapping("/concernedList")
    public Result<List<R_User>> getConcernedList(
            @RequestParam ("userId") String userId){
        List<R_User> list = userService.getConcernedList(userId);
        return Result.success(list);
    }

    @PostMapping("/likedList")
    public Result<List<R_OverviewContribution>> getLikedList(
            @RequestParam ("userId") String userId){
        List<R_OverviewContribution> list = userService.getLikedList(userId);
        return Result.success(list);
    }


    @PostMapping("/favouriteList")
    public Result<List<R_OverviewContribution>> getFavouriteList(
            @RequestParam ("userId") String userId){
        List<R_OverviewContribution> list = userService.getFavouriteList(userId);
        return Result.success(list);

    }

    @PostMapping("/userCommentList")
    public Result<List<R_UserComment>> getUserCommentList(
            @RequestParam ("userId") String userId){
        List<R_UserComment> list = userService.getUserCommentList(userId);
        return Result.success(list);

    }

    @PostMapping("/user/deleteComment")
    public Result<String> deleteComment(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("commentId") String commentId) throws Exception {
        String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
        boolean ok = userService.deleteComment(commentId, userId);
        if (ok) {
            return Result.success("删除评论成功");
        }
        return Result.error("删除评论失败");

    }

    @PostMapping("/user/deleteContribution")
    public Result<String> deleteContribution(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("contributionId") String contributionId) throws Exception {
        String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
        boolean ok = userService.deleteContribution(contributionId, userId);
        if (ok) {
            return Result.success("删除作品成功");
        }
        return Result.error("删除作品失败");

    }

    @PostMapping("/userInfo")
    public Result<R_UserInfoDTO> getUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("userId") String userId) throws Exception {
        String requesterId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
        R_UserInfoDTO data = userService.getUserInfo(requesterId,userId);
        return Result.success(data);
    }

    @PostMapping("/mySecurityIssues")
    public Result<List<String>> getMySecurityIssues(
            @RequestParam("username") String username){
        List<String> issues = userService.getMySecurityIssues(username);
        return Result.success(issues);

    }

    @PostMapping("/verifySecurityIssues")
    public Result<R_VerifySecurityIssuesDTO> verifySecurityIssues(
            @RequestParam("username") String username,
            @RequestPart(name = "SecurityIssues", required = false) List<R_SecurityIssue> SecurityIssues){
        R_VerifySecurityIssuesDTO data = userService.verifySecurityIssues(username, SecurityIssues);
        if(data.getVerified())
        {
            return Result.success(data);
        }
        return Result.error("密保验证失败");

    }

    @PostMapping("/updatePassword")
    public Result<String> updatePassword(
            @RequestHeader("Authorization") String tempToken,
            @RequestParam("username") String username,
            @RequestParam("newPassword") String newPassword) throws Exception {
        String tokenUsername = (String) TokenProcess.getAttributeFromToken(tempToken, "username");
        Integer type = (Integer) TokenProcess.getAttributeFromToken(tempToken, "type");
        boolean ok = userService.updatePassword(tokenUsername,type,username,newPassword);
        if (!ok) {
            return Result.error("密码修改失败");
        }
        return Result.success("密码修改成功");
    }

    /**
     * 更新用户基本信息
     * 路径: POST /user/updateUserInfo
     * 认证: 需要在请求头携带 Authorization: Bearer <JWT>
     *
     * 参数说明:
     * - authHeader: 请求头中的 Authorization，格式为 Bearer <token>
     * - newUsername: 新用户名，必填
     * - newGender: 新性别，必填。约定: 0=未知, 1=男, 2=女
     * - newAvatar: 新头像文件(可选)，multipart/form-data 中的文件字段名为 newAvatar
     *
     * 处理流程:
     * 1) 从 JWT 中解析出当前登录用户的 userId
     * 2) 调用 UserService 更新用户名/性别/头像(头像文件可为空)
     * 3) 根据更新结果返回统一响应
     *
     * 返回:
     * - 成功时返回 Result.success("更新成功")
     * - 失败时返回 Result.error("更新失败")
     *
     * 可能抛出:
     * - Exception: 当解析 Token 或处理文件过程中出现异常时
     */
    @PostMapping("/user/updateUserInfo")
    public Result<String> updateUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("newUsername") String newUsername,
            @RequestParam("newGender") Integer newGender,
            @RequestPart(name = "newAvatar", required = false) MultipartFile newAvatar) throws Exception {
        // 从 Authorization 的 Bearer Token 中解析 userId
        String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
        // 调用服务层执行业务更新(用户名/性别/头像)。头像参数可为空
        boolean ok = userService.updateUserInfo(userId, newUsername, newGender, newAvatar);
        // 根据业务结果返回统一响应
        if (ok) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @PostMapping("/user/concernUser")
    public Result<String> concernUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("concernedUserId") String concernedUserId) throws Exception {
        String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
        boolean ok = userService.concernUser(userId, concernedUserId);
        if (ok) {
            return Result.success("关注成功");
        }
        return Result.error("关注失败");

    }

    @PostMapping("/user/unconcernUser")
    public Result<String> unconcernUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("concernedUserId") String concernedUserId) throws Exception {
        String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
        boolean ok = userService.unconcernUser(userId, concernedUserId);
        if (ok) {
            return Result.success("已取消关注");
        }
        return Result.error("取消关注失败");

    }

}
