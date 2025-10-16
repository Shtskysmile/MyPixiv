package org.example.PCOI.Service.Inter;

import org.example.PCOI.Entity.Contribution;

import java.util.List;

public interface SuppliesService {


    List<Contribution> getAllSupplies(); // 获取所有物资列表

    boolean updateSupplies(Integer number, Integer SuppliesId);  // 租借设备
}
