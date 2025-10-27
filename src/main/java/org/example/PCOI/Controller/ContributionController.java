package org.example.PCOI.Controller;


import org.example.PCOI.Entity.OverviewContribution;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Service.Inter.LogService;
import org.example.PCOI.Service.Inter.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

// 补充Jwt工具导入
import org.example.PCOI.Utils.JwtUtil;

@RestController
public class ContributionController {
    @Autowired(required = false)
    private ContributionService contributionService;
    @Autowired
    private LogService logService;

    @GetMapping("/illustrations")
    public Result<List<OverviewContribution>> getIllustrations() {
        try {
            List<OverviewContribution> list = contributionService.getIllustrations();
            logService.logMethodExecution("contribution:getIllustrations");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取插画列表出错: " + e.getMessage());
        }
    }

    @GetMapping("/mangas")
    public Result<List<OverviewContribution>> getMangas() {
        try {
            List<OverviewContribution> list = contributionService.getMangas();
            logService.logMethodExecution("contribution:getMangas");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取漫画列表出错: " + e.getMessage());
        }
    }

    @PostMapping("/contribution")
    public Result<Map<String,Object>> getContribution(
            @RequestParam("contributionId") Integer contributionId) {
        try {
            Map<String, Object> data = contributionService.getContribution(contributionId);
            logService.logMethodExecution("contribution:getContribution");
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取作品详情出错: " + e.getMessage());
        }
    }

    @PostMapping("/unauditedContribution")
    public Result<Contribution> getUnauditedContribution(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("contributionId") Integer contributionId) {
        try {
            // 简单解析token存在即可
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                JwtUtil.parseToken(token);
            }
            Contribution c = contributionService.getUnauditedContribution(contributionId);
            logService.logMethodExecution("contribution:getUnauditedContribution");
            return Result.success(c);
        } catch (Exception e) {
            return Result.error("获取待审核作品出错: " + e.getMessage());
        }
    }

    @PostMapping("/contributionsRanking")
    public Result<List<OverviewContribution>> getContributionsRanking(
            @RequestParam("type") String type,
            @RequestParam("standard") String standard) {
        try {
            List<OverviewContribution> list = contributionService.getContributionsRanking(type, standard);
            logService.logMethodExecution("contribution:getContributionsRanking");
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取作品排行出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/likeContribution")
    public Result<String> likeContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId) {
        try {
            boolean ok = contributionService.likeContribution(userId, contributionId);
            if (ok) {
                logService.logMethodExecution("user:likeContribution");
                return Result.success("点赞成功");
            }
            return Result.error("点赞失败");
        } catch (Exception e) {
            return Result.error("点赞出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/unlikeContribution")
    public Result<String> unlikeContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId) {
        try {
            boolean ok = contributionService.unlikeContribution(userId, contributionId);
            if (ok) {
                logService.logMethodExecution("user:unlikeContribution");
                return Result.success("取消点赞成功");
            }
            return Result.error("取消点赞失败");
        } catch (Exception e) {
            return Result.error("取消点赞出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/favoriteContribution")
    public Result<String> favoriteContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId) {
        try {
            boolean ok = contributionService.favoriteContribution(userId, contributionId);
            if (ok) {
                logService.logMethodExecution("user:favoriteContribution");
                return Result.success("收藏成功");
            }
            return Result.error("收藏失败");
        } catch (Exception e) {
            return Result.error("收藏出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/unfavoriteContribution")
    public Result<String> unfavoriteContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId) {
        try {
            boolean ok = contributionService.unfavoriteContribution(userId, contributionId);
            if (ok) {
                logService.logMethodExecution("user:unfavoriteContribution");
                return Result.success("取消收藏成功");
            }
            return Result.error("取消收藏失败");
        } catch (Exception e) {
            return Result.error("取消收藏出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/commentContribution")
    public Result<String> commentContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId,
            @RequestParam("comment") String comment) {
        try {
            boolean ok = contributionService.commentContribution(userId, contributionId, comment);
            if (ok) {
                logService.logMethodExecution("user:commentContribution");
                return Result.success("评论成功");
            }
            return Result.error("评论失败");
        } catch (Exception e) {
            return Result.error("评论出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/uploadContribution")
    public Result<String> uploadContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam("images") List<MultipartFile> images) {
        try {
            boolean ok = contributionService.uploadContribution(userId, title, type, description, images);
            if (ok) {
                logService.logMethodExecution("user:uploadContribution");
                return Result.success("上传成功");
            }
            return Result.error("上传失败");
        } catch (Exception e) {
            return Result.error("上传出错: " + e.getMessage());
        }
    }




}
