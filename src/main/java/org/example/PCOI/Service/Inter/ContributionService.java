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
    R_ContributionDTO getContribution(String userId,String contributionId);
    R_Contribution getPendingContribution(String userId,String role,String contributionId);
    List<R_OverviewContribution> getContributionsRanking(String type, String key);

    boolean likeContribution(String userId, String contributionId);
    boolean unlikeContribution(String userId, String contributionId);
    boolean favoriteContribution(String userId, String contributionId);
    boolean unfavoriteContribution(String userId, String contributionId);
    boolean commentContribution(String userId, String contributionId, String comment);
    boolean uploadContribution(String userId, String title, String type, String description, List<MultipartFile> images);
}


