package org.example.PCOI.Service.Impl;

import org.example.PCOI.ResponseDTO.R_SearchDTO;
import org.example.PCOI.Service.Inter.SearchService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Override
    public R_SearchDTO search(String keyword, String isTag) {
        // TODO: 实现关键词/标签搜索
        return null;
    }

    @Override
    public List<MultipartFile> imageSearch(String imagePath) {
        // TODO: 实现以图搜图
        return List.of();
    }
}

