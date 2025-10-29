package org.example.PCOI.Controller;

import org.example.PCOI.ResponseDTO.Result;
import org.example.PCOI.Service.Inter.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/search")
    public Result<Map<String,Object>> search(
            @RequestParam("keyword") String keyword,
            @RequestParam("type") String type){
        try {
            Map<String, Object> data = searchService.search(keyword, type);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("搜索出错: " + e.getMessage());
        }
    }

    @PostMapping("/image")
    public Result<MultipartFile> imageSearch(
            @RequestParam("imagePath") String imagePath){
        try {
            MultipartFile file = searchService.imageSearch(imagePath);
            return Result.success(file);
        } catch (Exception e) {
            return Result.error("以图搜图出错: " + e.getMessage());
        }
    }

}
