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
    private UserService userservice;
    @Autowired
    private LogService logService;

    @PostMapping("/register")
    public Result<String> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("gender") String gender,
            @RequestParam("securityIssues") List<SecurityIssue> securityIssues,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar){

    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        User user = userservice.login(username, password);
        if (user != null) {
            Map<String, Object> claims = Map.of(
                    "userId", user.getUserId(),
                    "username", user.getUsername()
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

    @PostMapping("/myContributions")
    public Result<List<Contribution>> getMyContributions(
            @RequestParam ("userId") String userId){

    }

    @PostMapping("/userInfo")
    public Result<User> getUserInfo(
            @RequestParam ("userId") String userId) {
    }

    @PostMapping("/mySecurityIssues")
    public Result<List<String>> getMySecurityIssues(
            @RequestParam ("username") String username){

    }

    @PostMapping("/verifySecurityIssues")
    public Result<String> verifySecurityIssues(
            @RequestParam ("username") String username,
            @RequestParam ("securityIssues") List<SecurityIssue> securityIssues){

    }

    @PostMapping("/updatePwd")
    public Result<String> updatePassword(
            @RequestParam ("username") String username,
            @RequestParam ("newPassword") String newPassword){

    }

}
