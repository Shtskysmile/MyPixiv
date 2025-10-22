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
                return Result.success("注册成功");
            } else {
                return Result.error("注册失败，用户名可能已存在");
            }


    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        User user = userservice.login(username, password);
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
            return Result.success(result);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    @PostMapping("/contributionList")
    public Result<List<OverviewContribution>> getContributionList(
            @RequestParam ("userId") String userId){

    }

    @PostMapping("/myContributions")
    public Result<Map<String,Object>> getMyContributions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("userId") String userId){

    }
    @PostMapping("/concernedList")
    public Result<List<User>> getConcernedList(
            @RequestParam ("userId") String userId){

    }

    @PostMapping("/likedList")
    public Result<List<Contribution>> getLikedList(
            @RequestParam ("userId") String userId){

    }


    @PostMapping("/favouriteList")
    public Result<List<Contribution>> getFavouriteList(
            @RequestParam ("userId") String userId){


    }

    @PostMapping("/userCommentList")
    public Result<List<UserComment>> getUserCommentList(
            @RequestParam ("userId") String userId){

    }

    @PostMapping("/user/deleteComment")
    public Result<String> deleteComment(
            @RequestParam ("commentId") Integer commentId,
            @RequestParam ("userId") Integer userId){

    }

    @PostMapping("/user/deleteContribution")
    public Result<String> deleteContribution(
            @RequestParam ("contributionId") Integer contributionId,
            @RequestParam ("userId") Integer userId){

    }


    @PostMapping("/userInfo")
    public Result<Map<String,Object>> getUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam ("userId") String userId) {
    }

    @PostMapping("/mySecurityIssues")
    public Result<List<String>> getMySecurityIssues(
            @RequestParam ("username") String username){

    }

    @PostMapping("/verifySecurityIssues")
    public Result<Map<String,Object>> verifySecurityIssues(
            @RequestParam ("username") String username,
            @RequestParam ("securityIssues") List<SecurityIssue> securityIssues){
        // 验证密保问题通过后
//        Map<String, Object> claims = Map.of(
//                "username", username,
//                "type", "resetPwd"
//        );
//        String tempToken = JwtUtil.genToken(claims, 10 * 60 * 1000); // 10分钟有效
//        return Result.success(Map.of("tempToken", tempToken));

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
            userservice.updatePassword(username, newPassword);
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

    }

    @PostMapping("/user/concernUser")
    public Result<String> concernUser(
            @RequestParam ("userId") Integer userId,
            @RequestParam ("concernedUserId") Integer concernedUserId){

    }

    @PostMapping("/user/unconcernUser")
    public Result<String> unconcernUser(
            @RequestParam ("userId") Integer userId,
            @RequestParam ("concernedUserId") Integer concernedUserId){

    }

//    @PostMapping("/someApi")
//    public Result<?> someApi(@RequestHeader("Authorization") String authHeader) {
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            try {
//                Map<String, Object> claims = JwtUtil.parseToken(token);
//                // 现在可以使用 claims 中的用户信息
//                String username = (String) claims.get("username");
//                // 业务逻辑...
//                return Result.success(username);
//            } catch (Exception e) {
//                return Result.error("token无效");
//            }
//        }
//        return Result.error("未携带token");
//    }

}
