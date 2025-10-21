package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Log;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Entity.User;
import org.example.PCOI.Service.Inter.AdminService;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;
    @Autowired
    private LogService logService;

    @PostMapping("/search")
    public Result<Map<String,Object>> search(
            @RequestParam("keyword") String keyword,
            @RequestParam("type") String type,){
    }

}
