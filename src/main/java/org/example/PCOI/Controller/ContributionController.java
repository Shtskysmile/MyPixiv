package org.example.PCOI.Controller;
import org.example.PCOI.ResponseDTO.R_ContributionDTO;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.example.PCOI.ResponseDTO.Result;
import org.example.PCOI.ResponseDTO.R_Contribution;
import org.example.PCOI.Service.Inter.ContributionService;
import org.example.PCOI.Utils.TokenProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

// 补充Jwt工具导入
import org.example.PCOI.Utils.JwtUtil;

@RestController
public class ContributionController {
    @Autowired
    private ContributionService contributionService;

    @GetMapping("/illustrations")
    public Result<List<R_OverviewContribution>> getIllustrations() {
        try {
            List<R_OverviewContribution> list = contributionService.getIllustrations();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取插画列表出错: " + e.getMessage());
        }
    }

    @GetMapping("/mangas")
    public Result<List<R_OverviewContribution>> getMangas() {
        try {
            List<R_OverviewContribution> list = contributionService.getMangas();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取漫画列表出错: " + e.getMessage());
        }
    }

    @PostMapping("/contribution")
    public Result<R_ContributionDTO> getContribution(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("contributionId") String contributionId) {
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            R_ContributionDTO data = contributionService.getContribution(userId,contributionId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取作品详情出错: " + e.getMessage());
        }
    }

    @PostMapping("/pendingContribution")
    public Result<R_Contribution> getPendingContribution(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("contributionId") String contributionId) {
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            Integer role = (Integer) TokenProcess.getAttributeFromToken(authHeader, "role");
            R_Contribution c = contributionService.getPendingContribution(userId,role,contributionId);
            return Result.success(c);
        } catch (Exception e) {
            return Result.error("获取待审核作品出错: " + e.getMessage());
        }
    }

    @PostMapping("/contributionsRanking")
    public Result<List<R_OverviewContribution>> getContributionsRanking(
            @RequestParam("type") Integer type,
            @RequestParam("key") Integer key) {
        try {
            List<R_OverviewContribution> list = contributionService.getContributionsRanking(type, key);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取作品排行出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/likeContribution")
    public Result<String> likeContribution(
            @RequestHeader ("Authorization") String authHeader,
            @RequestParam("contributionId") String contributionId) {
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = contributionService.likeContribution(userId, contributionId);
            if (ok) {
                return Result.success("点赞成功");
            }
            return Result.error("点赞失败");
        } catch (Exception e) {
            return Result.error("点赞出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/unlikeContribution")
    public Result<String> unlikeContribution(
            @RequestHeader ("Authorization") String authHeader,
            @RequestParam("contributionId") String contributionId) {
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = contributionService.unlikeContribution(userId, contributionId);
            if (ok) {
                return Result.success("取消点赞成功");
            }
            return Result.error("取消点赞失败");
        } catch (Exception e) {
            return Result.error("取消点赞出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/favoriteContribution")
    public Result<String> favoriteContribution(
            @RequestHeader ("Authorization") String authHeader,
            @RequestParam("contributionId") String contributionId) {
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = contributionService.favoriteContribution(userId, contributionId);
            if (ok) {
                return Result.success("收藏成功");
            }
            return Result.error("收藏失败");
        } catch (Exception e) {
            return Result.error("收藏出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/unfavoriteContribution")
    public Result<String> unfavoriteContribution(
            @RequestHeader ("Authorization") String authHeader,
            @RequestParam("contributionId") String contributionId) {
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = contributionService.unfavoriteContribution(userId, contributionId);
            if (ok) {
                return Result.success("取消收藏成功");
            }
            return Result.error("取消收藏失败");
        } catch (Exception e) {
            return Result.error("取消收藏出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/commentContribution")
    public Result<String> commentContribution(
            @RequestHeader ("Authorization") String authHeader,
            @RequestParam("contributionId") String contributionId,
            @RequestParam("comment") String comment) {
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = contributionService.commentContribution(userId, contributionId, comment);
            if (ok) {
                return Result.success("评论成功");
            }
            return Result.error("评论失败");
        } catch (Exception e) {
            return Result.error("评论出错: " + e.getMessage());
        }
    }

    @PostMapping("/user/uploadContribution")
    public Result<String> uploadContribution(
            @RequestHeader ("Authorization") String authHeader,
            @RequestParam("title") String title,
            @RequestParam("type") Integer type,
            @RequestParam("description") String description,
            @RequestParam("images") List<MultipartFile> images) {
        try {
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            boolean ok = contributionService.uploadContribution(userId, title, type, description, images);
            if (ok) {
                return Result.success("上传成功");
            }
            return Result.error("上传失败");
        } catch (Exception e) {
            return Result.error("上传出错: " + e.getMessage());
        }
    }
}
