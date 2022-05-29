package com.foresight.pms.dto;


import com.foresight.pms.model.ProjectOrTask;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectHierarchyDto {
    public Iterable<ProjectOrTask> items;
}
