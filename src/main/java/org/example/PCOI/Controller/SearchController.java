package org.example.PCOI.Controller;

import org.example.PCOI.ResponseDTO.R_SearchDTO;
import org.example.PCOI.ResponseDTO.Result;
import org.example.PCOI.Service.Inter.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/search")
    public Result<R_SearchDTO> search(
            @RequestParam("keyword") String keyword,
            @RequestParam("isTag") Boolean isTag){
        R_SearchDTO data = searchService.search(keyword, isTag);
        return Result.success(data);
    }

    @PostMapping("/image")
    public Result<List<MultipartFile>> imageSearch(
            @RequestParam("imagePath") String imagePath) throws IOException {
        List<MultipartFile> files = searchService.imageSearch(imagePath);
        return Result.success(files);
    }

}
