package org.example.PCOI.Service.Inter;

import org.example.PCOI.ResponseDTO.R_SearchDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface SearchService {
    R_SearchDTO search(String keyword, Boolean isTag);
    List<MultipartFile> imageSearch(String imagePath) throws IOException;
}
