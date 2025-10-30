package org.example.PCOI.Service.Inter;

import org.example.PCOI.ResponseDTO.R_Contribution;
import org.example.PCOI.ResponseDTO.R_ContributionDTO;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ContributionService {
    List<R_OverviewContribution> getIllustrations();
    List<R_OverviewContribution> getMangas();
    R_ContributionDTO getContribution(Integer userId,Integer contributionId);
    R_Contribution getUnauditedContribution(Integer userId,String role,Integer contributionId);
    List<R_OverviewContribution> getContributionsRanking(String type, String key);

    boolean likeContribution(Integer userId, Integer contributionId);
    boolean unlikeContribution(Integer userId, Integer contributionId);
    boolean favoriteContribution(Integer userId, Integer contributionId);
    boolean unfavoriteContribution(Integer userId, Integer contributionId);
    boolean commentContribution(Integer userId, Integer contributionId, String comment);
    boolean uploadContribution(Integer userId, String title, String type, String description, List<MultipartFile> images);
}

