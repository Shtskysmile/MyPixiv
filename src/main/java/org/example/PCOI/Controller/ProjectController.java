package org.example.PCOI.Controller;

import org.example.PCOI.Entity.RequestStruct.CreateProjectRequest;
import org.example.PCOI.Entity.Result;
import org.example.PCOI.Service.Inter.LogService;
import org.example.PCOI.Service.Inter.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private LogService logService;

    @PostMapping("/createproject")
    public Result<String> createProject(@RequestBody CreateProjectRequest req) {
        String username =req.getUsername();
        String name = req.getName();
        String type = req.getType();
        String description = req.getDescription();
        String owner = req.getOwner();
        List<String> users = req.getUsers();
        String startDate = req.getStartDate();
        String endDate = req.getEndDate();
        Integer lab_id = req.getLab_id();
        logService.logMethodExecution(username);
        try {
            boolean success=projectService.createProject(username,name,type,description,owner,users,startDate,endDate,lab_id);
            if(success)
                return Result.success("Project created successfully!");
            else
                return Result.error("Authority denied!");
        } catch (Exception e) {
            return Result.error("Error creating project: " + e.getMessage());
        }
    }
    @GetMapping("/projects")
    public Result<List<Project>> getProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error("Error fetching projects: " + e.getMessage());
        }
    }

    @PostMapping("/projects/project")
    public Result<Project> getProjectById(@RequestBody Map<String, Integer> req) {
        Integer id = req.get("id");
        try {
            Project project = projectService.getProjectById(id);
            if (project != null) {
                return Result.success(project);
            } else {
                return Result.error("Project not found");
            }
        } catch (Exception e) {
            return Result.error("Error fetching project: " + e.getMessage());
        }
    }

}
