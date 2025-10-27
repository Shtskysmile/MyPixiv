package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.OverviewContribution;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ContributionService {
    List<OverviewContribution> getIllustrations();
    List<OverviewContribution> getMangas();
    Map<String, Object> getContribution(Integer contributionId);
    Contribution getUnauditedContribution(Integer contributionId);
    List<OverviewContribution> getContributionsRanking(String type, String standard);

    boolean likeContribution(Integer userId, Integer contributionId);
    boolean unlikeContribution(Integer userId, Integer contributionId);
    boolean favoriteContribution(Integer userId, Integer contributionId);
    boolean unfavoriteContribution(Integer userId, Integer contributionId);
    boolean commentContribution(Integer userId, Integer contributionId, String comment);
    boolean uploadContribution(Integer userId, String title, String type, String description, List<MultipartFile> images);
}

