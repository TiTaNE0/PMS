package com.foresight.pms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.foresight.pms.model.ProjectOrTaskEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ProjectTaskDto {
    @Id
    String uid;
    String name;
    @ApiModelProperty(example = "PROJECT")
    ProjectOrTaskEnum type;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;
    String parentUid;
}
