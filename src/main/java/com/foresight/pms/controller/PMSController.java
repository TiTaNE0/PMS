package com.foresight.pms.controller;

import com.foresight.pms.configuration.SwaggerConfiguration;
import com.foresight.pms.dto.CompletionStatusDto;
import com.foresight.pms.dto.DateUpdateDto;
import com.foresight.pms.dto.ProjectTaskDto;
import com.foresight.pms.model.ProjectOrTask;
import com.foresight.pms.service.IPmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("project/")
@Api(tags = {SwaggerConfiguration.DESCRIPTION})
public class PMSController {

    @Autowired
    IPmsService iPmsService;

    @ApiOperation(value = "Add new project or task providing parent's UID", notes = "Returns persisted project or task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project or task successfully persisted"),
            @ApiResponse(code = 403, message = "TASK can be added only to a project"),
            @ApiResponse(code = 404, message = "Project by UID not found!"),
            @ApiResponse(code = 409, message = "Project or task with provided UID exists"),
    })
    @PostMapping("{parentUid}")
    public ProjectTaskDto addProjectTask(@PathVariable String parentUid, @RequestBody ProjectTaskDto projectTask) {
        return iPmsService.addTaskOrSubproject(parentUid, projectTask);
    }

    @ApiOperation(value = "Add new project or task providing parent's UID", notes = "Returns persisted project or task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project or task successfully deleted"),
            @ApiResponse(code = 400, message = "Can't delete last task UID"),
            @ApiResponse(code = 404, message = "Project by UID not found!"),
    })
    @DeleteMapping("{uid}")
    public ProjectTaskDto deleteProjectTask(@PathVariable String uid) {
        return iPmsService.deleteTaskOrSubproject(uid);
    }

    @ApiOperation(value = "Update task's Start and End dates", notes = "Returns updated task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project or task successfully deleted"),
            @ApiResponse(code = 400, message = "Can't change project or subproject timeline"),
            @ApiResponse(code = 404, message = "Project by UID not found!"),
    })
    @PutMapping("dates/{uid}")
    public ProjectTaskDto updateTaskDates(@PathVariable String uid, @RequestBody DateUpdateDto dateUpdateDto) {
        return iPmsService.updateTaskDates(uid, dateUpdateDto);
    }

    @ApiOperation(value = "Retrieve projects or task completion status", notes = "Returns string in percent of completion")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Project by UID not found!"),
    })
    @GetMapping("status/")
    public CompletionStatusDto getCompletionStatus(@RequestParam String uid,
                                                   @RequestParam
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return iPmsService.getCompletionStatus(uid, date);
    }

    @ApiOperation(value = "Retrieves object consisting all projects and tasks ", notes = "Returns JSON formatted string")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal server error"),
    })
    @GetMapping("download/")
    public String getProjectHierarchy() {
        return iPmsService.returnProjectHierarchy();
    }

    @GetMapping("/all")
    public List<ProjectOrTask> getAllProjects() {
        return iPmsService.getAllProjects();
    }
}
