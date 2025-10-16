package org.example.PCOI.Controller;

import org.example.PCOI.Entity.RequestStruct.Rent_BackSuppRequest;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Service.Inter.LogService;
import org.example.PCOI.Service.Inter.SuppliesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SuppliesController {
    @Autowired
    private SuppliesService suppliesService;
    @Autowired
    private LogService logService;
    @GetMapping("/supplies")
    public Result<List<Contribution>> getSupplies() {
        try {
            List<Contribution> supplie = suppliesService.getAllSupplies();
            return Result.success(supplie);
        } catch (Exception e) {
            return Result.error("Error fetching equipments: " + e.getMessage());
        }
    }

    @PostMapping("/supplies/rent_backSupplies")
    public Result<String> rent_backSupplies(@RequestBody Rent_BackSuppRequest req)
    {

        String username = req.getUsername();
        Integer id = req.getId();
        Integer number = req.getNumber();
        logService.logMethodExecution(username);
        try{
            boolean success = suppliesService.updateSupplies(number,id);
            if(success)
                return Result.success("Rent_back SecurityIssue Success!");
            else
                return Result.error("Rent_back SecurityIssue Failed");
        } catch (Exception e) {
            return Result.error("Error fetching equipments: " + e.getMessage());
        }
    }


}
