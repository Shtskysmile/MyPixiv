package org.example.PCOI.Controller;


import org.example.PCOI.Entity.OverviewContribution;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Service.Inter.LogService;
import org.example.PCOI.Service.Inter.SuppliesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class ContributionController {
    @Autowired
    private ContributionService contributionService;
    @Autowired
    private LogService logService;

    @GetMapping("/illustrations")
    public Result<List<OverviewContribution>> getIllustrations() {

    }

    @GetMapping("/mangas")
    public Result<List<OverviewContribution>> getMangas() {

    }

    @PostMapping("/contribution")
    public Result<Map<String,Object>> getContribution(
            @RequestParam("contributionId") Integer contributionId) {

    }

    @PostMapping("/unauditedContribution")
    public Result<Contribution> getUnauditedContribution(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("contributionId") Integer contributionId) {

    }

    @PostMapping("/contributionsRanking")
    public Result<List<OverviewContribution>> getContributionsRanking(
            @RequestParam("type") String type,
            @RequestParam("standard") String standard) {

    }

    @PostMapping("/user/likeContribution")
    public Result<String> likeContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId) {

    }

    @PostMapping("/user/unlikeContribution")
    public Result<String> unlikeContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId) {

    }

    @PostMapping("/user/favoriteContribution")
    public Result<String> favoriteContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId) {

    }

    @PostMapping("/user/unfavoriteContribution")
    public Result<String> unfavoriteContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId) {

    }

    @PostMapping("/user/commentContribution")
    public Result<String> commentContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("contributionId") Integer contributionId,
            @RequestParam("comment") String comment) {

    }

    @PostMapping("/user/uploadContribution")
    public Result<String> uploadContribution(
            @RequestParam("userId") Integer userId,
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam("images") List<MultipartFile> images) {

    }




}
