package org.example.PCOI.Service.Inter;

import org.example.PCOI.ResponseDTO.R_SearchDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface SearchService {
    R_SearchDTO search(String keyword, String isTag);
    List<MultipartFile> imageSearch(String imagePath);
}

