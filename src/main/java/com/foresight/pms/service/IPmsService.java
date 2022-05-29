package com.foresight.pms.service;

import com.foresight.pms.dto.CompletionStatusDto;
import com.foresight.pms.dto.DateUpdateDto;
import com.foresight.pms.dto.ProjectTaskDto;
import com.foresight.pms.exceptions.ProjectNotFoundException;
import com.foresight.pms.model.ProjectOrTask;

import java.time.LocalDate;
import java.util.List;

public interface IPmsService {

    ProjectTaskDto addTaskOrSubproject(String parentUid, ProjectTaskDto projectTask);

    ProjectTaskDto deleteTaskOrSubproject(String uid) throws ProjectNotFoundException;

    CompletionStatusDto getCompletionStatus(String uid, LocalDate date);

    ProjectTaskDto updateTaskDates(String uid, DateUpdateDto dateUpdateDto);

    String returnProjectHierarchy();

    List<ProjectOrTask> getAllProjects();

    void adjustProjectTimeline(ProjectOrTask item);

    }
