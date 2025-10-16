package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.Tag;

import java.util.List;

public interface LaboratorysService {
   List<Tag> getLaboratorys();

   Tag getLaboratoryById(Integer id); // 根据ID获取实验室详情

   boolean backLaboratory(Integer id); // 更新实验室信息


}

