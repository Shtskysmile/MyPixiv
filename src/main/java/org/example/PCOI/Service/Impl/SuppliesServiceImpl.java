package org.example.PCOI.Service.Impl;

import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Mapper.SuppliesMapper;
import org.example.PCOI.Service.Inter.SuppliesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuppliesServiceImpl implements SuppliesService {

    @Autowired
    private SuppliesMapper suppliesMapper; // 假设有一个SuppliesMapper用于数据库操作
    @Override
    public List<Contribution> getAllSupplies(){
        return suppliesMapper.selectAllSupplies(); // 获取所有物资列表

    }
    @Override
    public boolean updateSupplies(Integer number, Integer SuppliesId){
        Contribution contribution = suppliesMapper.selectSuppliesById(SuppliesId);
        if (contribution == null ) {
            return false; // 物资不存在或库存不足
        }
        Integer currentQuantity = contribution.getQuantity();
        contribution.setQuantity(currentQuantity + number);
        suppliesMapper.updateSupplies(contribution); // 更新物资数量
        return true;

    }

}
