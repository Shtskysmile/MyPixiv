package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.Comment;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.*;
import org.example.PCOI.ResponseDTO.*;
import org.example.PCOI.Service.Inter.ContributionService;
import org.example.PCOI.Service.Support.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.example.PCOI.Service.Support.TransformService;
import java.util.ArrayList;
import java.util.List;


import static org.example.PCOI.Service.Support.Enum.*;

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
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public List<R_OverviewContribution> getIllustrations() {
        try{
            List<Contribution> contributions = contributionMapper.selectContributionsByType(illustration);
            List<R_OverviewContribution> rOverviewContributions = new ArrayList<>();
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
            List<Contribution> contributions = contributionMapper.selectContributionsByType(manga);
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
            User contributionUser = userMapper.selectUserById(contribution.getAuthorId());
            List<Comment> comments = commentMapper.selectCommentsByContributionId(contributionId);
            boolean isLiked = likeMapper.isLike(userId, contributionId);
            boolean isFavorite = favoriteMapper.isFavorite(userId, contributionId);
            R_Contribution rContribution = transformService.transformContributionToRContribution(contribution, contributionUser.getAvatar());
            List<R_ContributionComment> rContributionComments = new ArrayList<>();
            for (Comment comment : comments) {
                User commentUser = userMapper.selectUserById(comment.getAuthor());
                R_ContributionComment rContributionComment = transformService.transformCommentToRContributionComment(comment, commentUser.getAvatar());
                rContributionComments.add(rContributionComment);
            }
            R_ContributionDTO rContributionDTO = new R_ContributionDTO();
            rContributionDTO.setContribution(rContribution);
            rContributionDTO.setComments(rContributionComments);
            rContributionDTO.setIsLiked(isLiked);
            rContributionDTO.setIsFavorite(isFavorite);
            contribution.setViewCount(contribution.getViewCount()+1);
            contributionMapper.updateContribution(contribution);
            return rContributionDTO;
        } catch (Exception e) {
            log.error("Error fetching contribution details:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public R_Contribution getPendingContribution(String userId, Integer role, String contributionId) {
        try{
            Contribution contribution = contributionMapper.selectContributionById(contributionId);
            if(contribution!=null && (userId.equals(contribution.getAuthorId())||role.equals(communityAdmin)))
            {
                User contributionUser = userMapper.selectUserById(contribution.getAuthorId());
                return transformService.transformContributionToRContribution(contribution, contributionUser.getAvatar());
            }
            return null;
        }catch (Exception e){
            log.error("Error fetching pending contribution:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<R_OverviewContribution> getContributionsRanking(Integer type, Integer key) {
        try{
            int limit = maxSearchLimit;
            List<Contribution> contributions = switch (key) {
                case viewCount -> contributionMapper.selectContributionsByViewCount(limit);
                case favoriteCount -> contributionMapper.selectContributionsByFavoriteCount(limit);
                case likeCount -> contributionMapper.selectContributionsByLikeCount(limit);
                case commentCount -> contributionMapper.selectContributionsByCommentCount(limit);
                default -> List.of();
            };
            List<R_OverviewContribution> result = new ArrayList<>();
            for (Contribution c : contributions) {
                if (type != null && (type.equals(illustration) || type.equals(manga))) {
                    if (!type.equals(c.getType())) continue;
                }
                User author = userMapper.selectUserById(c.getAuthorId());
                String avatar = author == null ? null : author.getAvatar();
                result.add(transformService.transformContributionToROverviewContribution(c, avatar));
            }
            return result;
        } catch (Exception e) {
            log.error("Error fetching contributions ranking: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean likeContribution(String userId, String contributionId) {
        try{
            if(userMapper.selectUserById(userId)==null || contributionMapper.selectContributionById(contributionId)==null)
                return false;
            if(!likeMapper.isLike(userId,contributionId)){
                likeMapper.insertLike(userId,contributionId);
                Contribution contribution = contributionMapper.selectContributionById(contributionId);
                contribution.setLikeCount(contribution.getLikeCount()+1);
                contributionMapper.updateContribution(contribution);
                return true;
            }
            return true;
        }catch (Exception e){
            log.error("Error like contribution，userId：{}，contributionId：{}:{}", userId, contributionId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unlikeContribution(String userId, String contributionId) {
        try{
            if(userMapper.selectUserById(userId)==null || contributionMapper.selectContributionById(contributionId)==null)
                return false;
            if(likeMapper.isLike(userId,contributionId)){
                likeMapper.deleteLike(userId,contributionId);
                Contribution contribution = contributionMapper.selectContributionById(contributionId);
                contribution.setLikeCount(Math.max(0,contribution.getLikeCount()-1));
                contributionMapper.updateContribution(contribution);
                return true;
            }
            return true;
        }catch (Exception e){
            log.error("Error unlike contribution，userId：{}，contributionId：{}:{}", userId, contributionId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean favoriteContribution(String userId, String contributionId) {
        try{
            if(userMapper.selectUserById(userId)==null || contributionMapper.selectContributionById(contributionId)==null)
                return false;
            if(!favoriteMapper.isFavorite(userId,contributionId)){
                favoriteMapper.insertFavorite(userId,contributionId);
                Contribution contribution = contributionMapper.selectContributionById(contributionId);
                contribution.setFavoriteCount(contribution.getFavoriteCount()+1);
                contributionMapper.updateContribution(contribution);
                return true;
            }
            return true;
        }catch (Exception e){
            log.error("Error favorite contribution，userId：{}，contributionId：{}:{}", userId, contributionId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unfavoriteContribution(String userId, String contributionId) {
        try{
            if(userMapper.selectUserById(userId)==null || contributionMapper.selectContributionById(contributionId)==null)
                return false;
            if(favoriteMapper.isFavorite(userId,contributionId)){
                favoriteMapper.deleteFavorite(userId,contributionId);
                Contribution contribution = contributionMapper.selectContributionById(contributionId);
                contribution.setFavoriteCount(Math.max(0,contribution.getFavoriteCount()-1));
                contributionMapper.updateContribution(contribution);
                return true;
            }
            return true;
        }catch (Exception e){
            log.error("Error unfavorite contribution，userId：{}，contributionId：{}:{}", userId, contributionId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean commentContribution(String userId, String contributionId, String comment) {
        try{
            if(userMapper.selectUserById(userId)==null || contributionMapper.selectContributionById(contributionId)==null)
                return false;
            Comment newComment = new Comment();
            newComment.setAuthor(userId);
            newComment.setContribution(contributionId);
            newComment.setDescription(comment);
            commentMapper.insertComment(newComment);
            Contribution contribution = contributionMapper.selectContributionById(contributionId);
            contribution.setCommentCount(contribution.getCommentCount()+1);
            contributionMapper.updateContribution(contribution);
            return true;
        }catch (Exception e){
            log.error("Error comment contribution，userId：{}，contributionId：{}:{}", userId, contributionId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean uploadContribution(String userId, String title, Integer type, String description, List<MultipartFile> images) {
        try{
            if(userMapper.selectUserById(userId)==null)
                return false;
            Contribution newContribution = new Contribution();
            newContribution.setAuthorId(userId);
            newContribution.setTitle(title);
            newContribution.setType(type);
            newContribution.setDescription(description);
            newContribution.setStatus(normal);
            newContribution.setAuditStatus(pending);
            newContribution.setViewCount(0);
            newContribution.setFavoriteCount(0);
            newContribution.setLikeCount(0);
            newContribution.setCommentCount(0);
            String imagePath = fileStorageService.saveWorkImages(images,type,userId);
            newContribution.setImage(imagePath);
            contributionMapper.insertContribution(newContribution);
            return true;
        } catch (Exception e) {
            log.error("Error upload contribution，userId：{}:{}", userId, e.getMessage());
            return false;
        }
    }
}
