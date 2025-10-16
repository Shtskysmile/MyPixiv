package org.example.PCOI.Controller;

import org.example.PCOI.Entity.Tag;
import org.example.PCOI.Entity.RequestStruct.BackLaboratoryRequest;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Service.Inter.LaboratorysService;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController()
public class LaboratorysController {
    @Autowired
    private LaboratorysService laboratorysService;
    @Autowired
    private LogService logService;

    @GetMapping("/laboratorys")
    public Result<List<Tag>> getLaboratorys() {

        try {
            List<Tag> tagList = laboratorysService.getLaboratorys();
            return Result.success(tagList);
            } catch (Exception e) {
            return Result.error("Error fetching laboratorys: " + e.getMessage());
        }
    }

    @GetMapping("/laboratorys/laboratory")
    public Result<Tag> getLaboratorysById(@RequestBody Map<String, Integer> req) {
        Integer lab_id = req.get("lab_id");
        try {
            Tag tag = laboratorysService.getLaboratoryById(lab_id);
            if (tag != null) {
                return Result.success(tag);
            } else {
                return Result.error("Laboratory not found");
            }
        } catch (Exception e) {
            return Result.error("Error fetching laboratory: " + e.getMessage());
        }
    }

    @PostMapping("/mylaboratorys/backlaboratory")
    public Result<String> backLaboratory(@RequestBody BackLaboratoryRequest req) {
        Integer lab_id = req.getLab_id();
        String username = req.getUsername();
        logService.logMethodExecution(username);
        try {
            boolean success = laboratorysService.backLaboratory(lab_id);
            if (success) {
                return Result.success("Back Laboratory Success!");
            } else {
                return Result.error("Back Laboratory Failed");
            }
        } catch (Exception e) {
            return Result.error("Error returning laboratory: " + e.getMessage());
        }
    }

}
