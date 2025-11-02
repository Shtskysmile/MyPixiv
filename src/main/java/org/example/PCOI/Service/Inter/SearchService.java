package org.example.PCOI.Service.Inter;

import org.example.PCOI.ResponseDTO.R_SearchDTO;


public interface SearchService {
    R_SearchDTO search(String keyword, Boolean isTag);
}
