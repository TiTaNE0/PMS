package com.foresight.pms.dao;

import com.foresight.pms.model.ProjectOrTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectManagementRepository extends JpaRepository<ProjectOrTask, String> {

    public List<ProjectOrTask> findProjectOrTasksByParentUid(String parentUid);

}
