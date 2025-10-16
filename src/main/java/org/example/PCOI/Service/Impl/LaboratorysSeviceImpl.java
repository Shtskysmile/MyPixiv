package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Mapper.LaboratorysMapper;
import org.example.PCOI.Mapper.UserMapper;
import org.example.PCOI.Service.Inter.LaboratorysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class LaboratorysSeviceImpl implements LaboratorysService {
    @Autowired
    private LaboratorysMapper laboratorsmapper;

    @Autowired
    private UserMapper usermapper;

    @Override
    public List<Tag> getLaboratorys(){
        return laboratorsmapper.selectAllLaboratorys();
    }

    public Tag getLaboratoryById(Integer id) {

        return laboratorsmapper.selectLaboratoryById(id);
    }

    @Override
    public boolean backLaboratory(Integer id)
    {
        Tag tag = laboratorsmapper.selectLaboratoryById(id);
        if (tag == null) {
            return false; // 实验室不存在
        }

        // 检查实验室是否正在使用中
        if (!"使用中".equals(tag.getStatus())) {
            return false; // 实验室未被租借
        }

        // 更新实验室状态为“空闲”
        tag.setStatus("空闲");
        tag.setProject(null); // 清除关联的项目
        laboratorsmapper.updateLaboratory(tag);

        return true;
    }


}
