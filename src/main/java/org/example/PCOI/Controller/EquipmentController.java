package org.example.PCOI.Controller;

import org.example.PCOI.Entity.SecurityIssue;
import org.example.PCOI.Entity.RequestStruct.Rent_BackEquipRequest;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Service.Inter.EquipmentService;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private LogService logService;
    @GetMapping("/equipments")
    public Result<List<SecurityIssue>> getEquipments() {
        try {
            List<SecurityIssue> securityIssues = equipmentService.getAllEquipments();
            return Result.success(securityIssues);
        } catch (Exception e) {
            return Result.error("Error fetching equipments: " + e.getMessage());
        }
    }

    @PostMapping("/equipments/rentEquipment")
    public Result<String> rentEquipment(@RequestBody Rent_BackEquipRequest req)
    {
        String username = req.getUsername();
        Integer equip_id = req.getId();
        logService.logMethodExecution(username);
        try{
            boolean success = equipmentService.rentEquipments(username,equip_id);
            if(success)
                return Result.success("Rent SecurityIssue Success!");
            else
                return Result.error("Rent SecurityIssue Failed");
        } catch (Exception e) {
            return Result.error("Error fetching equipments: " + e.getMessage());
        }
    }

    @PostMapping("/myEquipment/backEquipment")
    public Result<String> backEquipment(@RequestBody Rent_BackEquipRequest req) {
        String username = req.getUsername();
        logService.logMethodExecution(username);
        Integer equip_id = req.getId();
        try {
            boolean success = equipmentService.backEquipments(equip_id);
            if (success)
                return Result.success("Back SecurityIssue Success!");
            else
                return Result.error("Back SecurityIssue Failed");
        } catch (Exception e) {
            return Result.error("Error returning equipment: " + e.getMessage());
        }
    }

    @PostMapping("/myEquipment")
    public Result<List<SecurityIssue>> getMyEquipments(@RequestBody Map<String,String> req)
    {
        String username = req.get("username");
        logService.logMethodExecution(username);
        try{
            List<SecurityIssue> mySecurityIssues = equipmentService.getEquipmentByUsername(username);
            if(mySecurityIssues !=null)
                return Result.success(mySecurityIssues);
            else
                return Result.error("get myequipments failed ");
        } catch (Exception e) {
            return Result.error("Error get myequipments: " + e.getMessage());
        }
    }

}
