package org.example.PCOI.Controller;

import org.example.PCOI.Entity.OverviewContribution;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Service.Inter.CommunityAdminService;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CommunityAdminController {
    @Autowired(required = false)
    private CommunityAdminService communityAdminService;
    @Autowired(required = false)
    private LogService logService;

    @PostMapping("/communityAdmin/blockUser")
    public Result<String> blockUser(
            @RequestParam("userId") Integer userId) {
        try {
            boolean ok = communityAdminService.blockUser(userId);
            if (ok) {
                logService.logMethodExecution("communityAdmin:blockUser");
                return Result.success("封禁用户成功");
            }
            return Result.error("封禁用户失败");
        } catch (Exception e) {
            return Result.error("封禁用户出错: " + e.getMessage());
        }
    }

    @PostMapping("/communityAdmin/unblockUser")
    public Result<String> unblockUser(
            @RequestParam("userId") Integer userId) {
        try {
            boolean ok = communityAdminService.unblockUser(userId);
            if (ok) {
                logService.logMethodExecution("communityAdmin:unblockUser");
                return Result.success("解封用户成功");
            }
            return Result.error("解封用户失败");
        } catch (Exception e) {
            return Result.error("解封用户出错: " + e.getMessage());
        }
    }

    @PostMapping("/communityAdmin/blockContribution")
    public Result<String> blockContribution(
            @RequestParam("contributionId") Integer contributionId) {
        try {
            boolean ok = communityAdminService.blockContribution(contributionId);
            if (ok) {
                logService.logMethodExecution("communityAdmin:blockContribution");
                return Result.success("封禁作品成功");
            }
            return Result.error("封禁作品失败");
        } catch (Exception e) {
            return Result.error("封禁作品出错: " + e.getMessage());
        }
    }

    @PostMapping("/communityAdmin/unblockContribution")
    public Result<String> unblockContribution(
            @RequestParam("contributionId") Integer contributionId) {
        try {
            boolean ok = communityAdminService.unblockContribution(contributionId);
            if (ok) {
                logService.logMethodExecution("communityAdmin:unblockContribution");
                return Result.success("解封作品成功");
            }
            return Result.error("解封作品失败");
        } catch (Exception e) {
            return Result.error("解封作品出错: " + e.getMessage());
        }
    }

    @GetMapping("/communityAdmin/auditContributions")
    public Result<Map<String,Object>> auditContributions() {
        try {
            Map<String, Object> data = communityAdminService.auditContributions();
            logService.logMethodExecution("communityAdmin:auditContributions");
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取待审核作品出错: " + e.getMessage());
        }
    }

    @PostMapping("/communityAdmin/dismissContribution")
    public Result<String> dismissContribution(
            @RequestParam("contributionId") Integer contributionId,
            @RequestParam("dismissReason") String dismissReason) {
        try {
            boolean ok = communityAdminService.dismissContribution(contributionId, dismissReason);
            if (ok) {
                logService.logMethodExecution("communityAdmin:dismissContribution");
                return Result.success("已驳回作品");
            }
            return Result.error("驳回作品失败");
        } catch (Exception e) {
            return Result.error("驳回作品出错: " + e.getMessage());
        }
    }

    @PostMapping("/communityAdmin/approveContribution")
    public Result<String> approveContribution(
            @RequestParam("contributionId") Integer contributionId) {
        try {
            boolean ok = communityAdminService.approveContribution(contributionId);
            if (ok) {
                logService.logMethodExecution("communityAdmin:approveContribution");
                return Result.success("已通过审核");
            }
            return Result.error("通过审核失败");
        } catch (Exception e) {
            return Result.error("通过审核出错: " + e.getMessage());
        }
    }

    @GetMapping("/communityAdmin/blockedUsers")
    public Result<List<User>> getBlockedUsers() {
        try {
            List<User> list = communityAdminService.getBlockedUsers();
            logService.logMethodExecution("communityAdmin:blockedUsers");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取封禁用户列表出错: " + e.getMessage());
        }
    }

    @GetMapping("/communityAdmin/blockedContributions")
    public Result<List<OverviewContribution>> getBlockedContributions() {
        try {
            List<OverviewContribution> list = communityAdminService.getBlockedContributions();
            logService.logMethodExecution("communityAdmin:blockedContributions");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取封禁作品列表出错: " + e.getMessage());
        }
    }

    @PostMapping("/communityAdmin/deleteComment")
    public Result<String> deleteComment(
            @RequestParam("commentId") Integer commentId) {
        try {
            boolean ok = communityAdminService.deleteComment(commentId);
            if (ok) {
                logService.logMethodExecution("communityAdmin:deleteComment");
                return Result.success("删除评论成功");
            }
            return Result.error("删除评论失败");
        } catch (Exception e) {
            return Result.error("删除评论出错: " + e.getMessage());
        }
    }

}
