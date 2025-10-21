package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.OverviewContribution;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Service.Inter.AdminService;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class CommunityAdminController {
    @Autowired
    private CommunityAdminService communityAdminService;
    @Autowired
    private LogService logService;

    @PostMapping("/communityAdmin/blockUser")
    public Result<String> blockUser(
            @RequestParam("userId") Integer userId) {
    }

    @PostMapping("/communityAdmin/unblockUser")
    public Result<String> unblockUser(
            @RequestParam("userId") Integer userId) {
    }

    @PostMapping("/communityAdmin/blockContribution")
    public Result<String> blockContribution(
            @RequestParam("contributionId") Integer contributionId) {
    }

    @PostMapping("/communityAdmin/unblockContribution")
    public Result<String> unblockContribution(
            @RequestParam("contributionId") Integer contributionId) {
    }

    @GetMapping("/communityAdmin/auditContributions")
    public Result<Map<String,Object>> auditContributions() {
    }

    @PostMapping("/communityAdmin/dismissContribution")
    public Result<String> dismissContribution(
            @RequestParam("contributionId") Integer contributionId,
            @RequestParam("dismissReason") String dismissReason) {
    }

    @PostMapping("/communityAdmin/approveContribution")
    public Result<String> approveContribution(
            @RequestParam("contributionId") Integer contributionId) {
    }

    @GetMapping("/communityAdmin/blockedUsers")
    public Result<List<User>> getBlockedUsers() {
    }

    @GetMapping("/communityAdmin/blockedContributions")
    public Result<List<OverviewContribution>> getBlockedContributions() {
    }

    @PostMapping("/communityAdmin/deleteComment")
    public Result<String> deleteComment(
            @RequestParam("commentId") Integer commentId) {
    }

}
