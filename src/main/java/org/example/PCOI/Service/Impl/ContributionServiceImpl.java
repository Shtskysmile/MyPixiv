package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.Comment;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.*;
import org.example.PCOI.ResponseDTO.R_Contribution;
import org.example.PCOI.ResponseDTO.R_ContributionDTO;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.example.PCOI.ResponseDTO.R_UserComment;
import org.example.PCOI.Service.Inter.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.example.PCOI.Service.Support.TransformService;
import java.util.ArrayList;
import java.util.List;

import static org.example.PCOI.Service.Support.Enum.illustration;

@Slf4j
@Service
public class ContributionServiceImpl implements ContributionService {
    @Autowired
    private ContributionMapper contributionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TransformService transformService;
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public List<R_OverviewContribution> getIllustrations() {
        try{
            List<Contribution> contributions = contributionMapper.selectContributionsByType(illustration);
            List<R_OverviewContribution> rOverviewContributions = null;
            for (Contribution contribution : contributions) {
                User contributionUser = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, contributionUser.getAvatar());
                rOverviewContributions.add(rOverviewContribution);
            }
            return rOverviewContributions;
        } catch (Exception e) {
            log.error("Error fetching illustrations:{}", e.getMessage());
            return List.of();
        }

    }

    @Override
    public List<R_OverviewContribution> getMangas() {
        try {
            List<Contribution> contributions = contributionMapper.selectContributionsByType(illustration);
            List<R_OverviewContribution> rOverviewContributions = new ArrayList<>();
            for (Contribution contribution : contributions) {
                User contributionUser = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, contributionUser.getAvatar());
                rOverviewContributions.add(rOverviewContribution);
            }
            return rOverviewContributions;
        } catch (Exception e) {
            log.error("Error fetching mangas:{}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public R_ContributionDTO getContribution(String userId, String contributionId) {
        try {
            Contribution contribution = contributionMapper.selectContributionById(contributionId);
            User User = userMapper.selectUserById(userId);
            List< Comment> comments = commentMapper.selectCommentsByContributionId(contributionId);
            boolean isLiked = likeMapper.isLike(userId, contributionId);
            boolean isFavorite = favoriteMapper.isFavorite(userId, contributionId);
            R_Contribution rContribution = transformService.transformContributionToRContribution(contribution);
            List<R_UserComment> rUserComments = null;
            for (Comment comment : comments) {
                User contributionUser = userMapper.selectUserById(contribution.getAuthorId());
                User commentUser = userMapper.selectUserById(comment.getAuthor());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, contributionUser.getAvatar());
                R_UserComment rUserComment = transformService.transformCommentToRUserComment(comment, rOverviewContribution,commentUser.getAvatar());
                rUserComments.add(rUserComment);
            }
            R_ContributionDTO rContributionDTO = new R_ContributionDTO();
            rContributionDTO.setContribution(rContribution);
            rContributionDTO.setComments(rUserComments);
            rContributionDTO.setIsLiked(isLiked);
            rContributionDTO.setIsFavorite(isFavorite);
            return rContributionDTO;
        } catch (Exception e) {
            log.error("Error fetching contribution details:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public R_Contribution getPendingContribution(String userId, Integer role, String contributionId) {
        // TODO: 实现获取待审核作品逻辑（根据角色校验权限）
        return null;
    }

    @Override
    public List<R_OverviewContribution> getContributionsRanking(Integer type, Integer key) {
        // TODO: 实现排行榜查询逻辑
        return List.of();
    }

    @Override
    public boolean likeContribution(String userId, String contributionId) {
        // TODO: 实现点赞逻辑（幂等处理）
        return false;
    }

    @Override
    public boolean unlikeContribution(String userId, String contributionId) {
        // TODO: 实现取消点赞逻辑（幂等处理）
        return false;
    }

    @Override
    public boolean favoriteContribution(String userId, String contributionId) {
        // TODO: 实现收藏逻辑（幂等处理）
        return false;
    }

    @Override
    public boolean unfavoriteContribution(String userId, String contributionId) {
        // TODO: 实现取消收藏逻辑（幂等处理）
        return false;
    }

    @Override
    public boolean commentContribution(String userId, String contributionId, String comment) {
        // TODO: 实现评论逻辑（内容校验、防刷）
        return false;
    }

    @Override
    public boolean uploadContribution(String userId, String title, Integer type, String description, List<MultipartFile> images) {
        // TODO: 实现作品上传逻辑（文件校验、存储、事务）
        return false;
    }
}

