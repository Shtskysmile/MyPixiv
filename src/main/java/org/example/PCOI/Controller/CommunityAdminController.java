package org.example.PCOI.Controller;

import org.example.PCOI.ResponseDTO.R_Audit_My_ContributionsDTO;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.example.PCOI.ResponseDTO.Result;
import org.example.PCOI.ResponseDTO.R_User;
import org.example.PCOI.Service.Inter.CommunityAdminService;
import org.example.PCOI.Utils.TokenProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CommunityAdminController {
    @Autowired
    private CommunityAdminService communityAdminService;

    @PostMapping("/communityAdmin/blockUser")
    public Result<String> blockUser(
            @RequestParam("userId") Integer userId) {
        try {
            boolean ok = communityAdminService.blockUser(userId);
            if (ok) {
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
                return Result.success("解封作品成功");
            }
            return Result.error("解封作品失败");
        } catch (Exception e) {
            return Result.error("解封作品出错: " + e.getMessage());
        }
    }

    @GetMapping("/communityAdmin/auditContributions")
    public Result<R_Audit_My_ContributionsDTO> auditContributions() {
        try {
            R_Audit_My_ContributionsDTO data = communityAdminService.auditContributions();
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取待审核作品出错: " + e.getMessage());
        }
    }

    @PostMapping("/communityAdmin/dismissContribution")
    public Result<String> dismissContribution(
            @RequestParam("contributionId") Integer contributionId,
            @RequestParam("dismissalReason") String dismissalReason) {
        try {
            boolean ok = communityAdminService.dismissContribution(contributionId, dismissalReason);
            if (ok) {
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
                return Result.success("已通过审核");
            }
            return Result.error("通过审核失败");
        } catch (Exception e) {
            return Result.error("通过审核出错: " + e.getMessage());
        }
    }

    @GetMapping("/communityAdmin/blockedUsers")
    public Result<List<R_User>> getBlockedUsers() {
        try {
            List<R_User> list = communityAdminService.getBlockedUsers();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取封禁用户列表出错: " + e.getMessage());
        }
    }

    @GetMapping("/communityAdmin/blockedContributions")
    public Result<List<R_OverviewContribution>> getBlockedContributions() {
        try {
            List<R_OverviewContribution> list = communityAdminService.getBlockedContributions();
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
                return Result.success("删除评论成功");
            }
            return Result.error("删除评论失败");
        } catch (Exception e) {
            return Result.error("删除评论出错: " + e.getMessage());
        }
    }

}
