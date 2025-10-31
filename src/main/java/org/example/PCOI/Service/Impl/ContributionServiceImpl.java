package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.ContributionMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.ResponseDTO.R_Contribution;
import org.example.PCOI.ResponseDTO.R_ContributionDTO;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.example.PCOI.Service.Inter.ContributionService;
import org.example.PCOI.Service.Support.TransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.example.PCOI.Service.Support.Enum.illustration;
import static org.example.PCOI.Service.Support.Enum.manga;

@Slf4j
@Service
public class ContributionServiceImpl implements ContributionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ContributionMapper contributionMapper;
    @Autowired
    private TransformService transformService;


    @Override
    public List<R_OverviewContribution> getIllustrations() {
        try{
            List<Contribution> contributions = contributionMapper.selectContributionsByType(illustration);
            List<R_OverviewContribution> rOverviewContributions = null;
            for (Contribution contribution : contributions) {
                User user = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution,user.getAvatar());
                rOverviewContributions.add(rOverviewContribution);
            }
            return rOverviewContributions;
        } catch (Exception e) {
            log.error("获取插画列表失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<R_OverviewContribution> getMangas() {
        try{
            List<Contribution> contributions = contributionMapper.selectContributionsByType(manga);
            List<R_OverviewContribution> rOverviewContributions = null;
            for (Contribution contribution : contributions) {
                User user = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution,user.getAvatar());
                rOverviewContributions.add(rOverviewContribution);
            }
            return rOverviewContributions;
        } catch (Exception e) {
            log.error("获取插画列表失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public R_ContributionDTO getContribution(String userId, String contributionId) {

        return null;
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

