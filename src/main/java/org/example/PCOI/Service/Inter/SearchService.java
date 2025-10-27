package org.example.PCOI.Service.Inter;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SearchService {
    Map<String, Object> search(String keyword, String type);
    MultipartFile imageSearch(String imagePath);
}

