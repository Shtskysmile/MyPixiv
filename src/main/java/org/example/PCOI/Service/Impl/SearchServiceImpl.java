package org.example.PCOI.Service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Mapper.ContributionMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.ResponseDTO.R_OverviewContribution;
import org.example.PCOI.ResponseDTO.R_SearchDTO;
import org.example.PCOI.ResponseDTO.R_User;
import org.example.PCOI.Service.Inter.SearchService;
import org.example.PCOI.Service.Support.FileFetchService;

import org.example.PCOI.Service.Support.TransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static org.example.PCOI.Service.Support.Enum.*;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ContributionMapper contributionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TransformService transformService;
    @Autowired
    private FileFetchService fileFetchService;


    @Override
    public R_SearchDTO search(String keyword, Boolean isTag) {
        List<Contribution> contributions;
        List<R_OverviewContribution> illustrations = new ArrayList<>();
        List<R_OverviewContribution> mangas = new ArrayList<>();
        List<R_User> rUsers = new ArrayList<>();
        R_SearchDTO rSearchDTO = new R_SearchDTO();
        if (isTag) {
            contributions = contributionMapper.selectContributionsByTag(keyword);
            for (Contribution contribution : contributions) {
                User user = userMapper.selectUserById(contribution.getAuthorId());
                R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
                if (contribution.getType().equals(illustration))
                    illustrations.add(rOverviewContribution);
                else if (contribution.getType().equals(manga))
                    mangas.add(rOverviewContribution);
            }
            rSearchDTO.setIllustrations(illustrations);
            rSearchDTO.setMangas(mangas);
            rSearchDTO.setUsers(null);
            return rSearchDTO;
        }
        contributions = contributionMapper.selectContributionsByTitle(maxSearchLimit,keyword);
        contributions.add(contributionMapper.selectContributionById(keyword));
        List<User> users = userMapper.selectUsersByName(maxSearchLimit,keyword);
        users.add(userMapper.selectUserById(keyword));
        for(Contribution contribution:contributions) {
            User user = userMapper.selectUserById(contribution.getAuthorId());
            R_OverviewContribution rOverviewContribution = transformService.transformContributionToROverviewContribution(contribution, user.getAvatar());
            if (contribution.getType().equals(illustration))
                illustrations.add(rOverviewContribution);
            else if (contribution.getType().equals(manga))
                mangas.add(rOverviewContribution);
        }
        for(User user:users) {
            R_User rUser = transformService.transformUserToRUser(user);
            rUsers.add(rUser);
        }
        rSearchDTO.setIllustrations(illustrations);
        rSearchDTO.setMangas(mangas);
        rSearchDTO.setUsers(rUsers);
        return rSearchDTO;
    }

    @Override
    public List<MultipartFile> imageSearch(String imagePath) throws IOException {
        return fileFetchService.loadImages(imagePath);
    }
}
