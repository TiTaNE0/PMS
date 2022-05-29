package com.foresight.pms.service;

import com.foresight.pms.dao.ProjectManagementRepository;
import com.foresight.pms.dto.CompletionStatusDto;
import com.foresight.pms.dto.DateUpdateDto;
import com.foresight.pms.dto.ProjectHierarchyDto;
import com.foresight.pms.dto.ProjectTaskDto;
import com.foresight.pms.exceptions.ForbiddenOperationException;
import com.foresight.pms.exceptions.ProjectNotFoundException;
import com.foresight.pms.model.ProjectOrTask;
import com.foresight.pms.model.ProjectOrTaskEnum;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class PmsServiceImpl implements IPmsService {
    private final ProjectManagementRepository projectManagementRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PmsServiceImpl(ModelMapper modelMapper, ProjectManagementRepository projectManagementRepository) {
        this.projectManagementRepository = projectManagementRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ProjectTaskDto addTaskOrSubproject(String parentUid, ProjectTaskDto projectTask) {
        if (projectManagementRepository.existsById(projectTask.getUid()))
            throw new ForbiddenOperationException(projectTask.getUid() + " exist!");
        if (projectTask.getType().equals(ProjectOrTaskEnum.TASK) & !hasValidParent(parentUid, projectTask))
            throw new ForbiddenOperationException("No valid parent");
        extendTimeline(parentUid, projectTask);
        ProjectOrTask item = modelMapper.map(projectTask, ProjectOrTask.class);
        item.setParentUid(parentUid);
        projectManagementRepository.save(item);
        return modelMapper.map(item, ProjectTaskDto.class);
    }

    private boolean hasValidParent(String parentUid, ProjectTaskDto projectTask) {
        if (parentUid == null & projectTask.getType().equals(ProjectOrTaskEnum.TASK))
            throw new ForbiddenOperationException("TASK can be added only to a project");

        if (!projectManagementRepository.existsById(Objects.requireNonNull(parentUid))
                & projectTask.getType() == ProjectOrTaskEnum.TASK)
            throw new ForbiddenOperationException("Project UID = " + projectTask.getUid() + " not exist");

        return true;
    }

    private void extendTimeline(String parentUid, ProjectTaskDto projectTask) {
        ProjectOrTask item = projectManagementRepository.findById(parentUid).orElseThrow(() -> new ProjectNotFoundException(parentUid));
        if (item.getStartDate().isAfter(projectTask.getStartDate()))
            item.setStartDate(projectTask.getStartDate());
        if (item.getEndDate().isBefore(projectTask.getEndDate()))
            item.setEndDate(projectTask.getStartDate());
        projectManagementRepository.save(item);
    }

    @Override
    public ProjectTaskDto deleteTaskOrSubproject(String uid) {
        ProjectOrTask item = projectManagementRepository.findById(uid).orElseThrow(() -> new ProjectNotFoundException(uid));
        if (isLastTask(item))
            throw new ForbiddenOperationException("Can't delete last task UID " + item.getUid() + " of Project" + item.getParentUid());
        projectManagementRepository.delete(item);
        adjustProjectTimeline(item);
        return modelMapper.map(item, ProjectTaskDto.class);
    }

    @Override
    @Transactional
    public CompletionStatusDto getCompletionStatus(String uid, LocalDate date) {
        ProjectOrTask item = projectManagementRepository.findById(uid).orElseThrow(() -> new ProjectNotFoundException(uid));
        int roundPercentage = (int)Math.floor(item.getCompletionStatusOnDate(date)*100);
        String status = roundPercentage + "%";
        return new CompletionStatusDto(status);
    }

    @Override
    @Transactional
    public ProjectTaskDto updateTaskDates(String uid, DateUpdateDto dateUpdateDto) {
        ProjectOrTask item = projectManagementRepository.findById(uid).orElseThrow(() -> new ProjectNotFoundException(uid));
        if (item.getType().equals(ProjectOrTaskEnum.PROJECT))
            throw new ForbiddenOperationException("Can't change project or subproject timeline");
        if (dateUpdateDto.getStartDate() != null) {
            item.setStartDate(dateUpdateDto.getStartDate());
        }
        if (dateUpdateDto.getEndDate() != null) {
            item.setEndDate(dateUpdateDto.getEndDate());
        }
        projectManagementRepository.save(item);
        return modelMapper.map(item, ProjectTaskDto.class);
    }

    @Override
    public String returnProjectHierarchy() {
        List<ProjectOrTask> items = projectManagementRepository.findAll();
        ProjectHierarchyDto projectHierarchyDto = new ProjectHierarchyDto(items);
        return new GsonBuilder()
                .registerTypeAdapter(
                        LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) ->
                                new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .setPrettyPrinting()
                .create()
                .toJson(projectHierarchyDto);
    }

    public List<ProjectOrTask> getAllProjects() {
        return projectManagementRepository.findAll();
    }

    private List<ProjectOrTask> getAllTasksByParentUid(String parentUid) {
        return projectManagementRepository.findProjectOrTasksByParentUid(parentUid);
    }

    public void adjustProjectTimeline(ProjectOrTask item) {
        ProjectOrTask parent = projectManagementRepository.findById(item.getParentUid()).orElseThrow(() -> new ProjectNotFoundException(item.getParentUid()));
        for (ProjectOrTask task: getAllTasksByParentUid(item.getParentUid())) {
            if (task.getStartDate().isBefore(parent.getStartDate()))
                parent.setStartDate(task.getStartDate());
            if (task.getEndDate().isAfter(parent.getEndDate()))
                parent.setEndDate(task.getEndDate());
        }
        projectManagementRepository.save(parent);
    }
    private boolean isLastTask(ProjectOrTask item) {
        return getAllTasksByParentUid(item.getParentUid()).size() <= 1;
    }

}
