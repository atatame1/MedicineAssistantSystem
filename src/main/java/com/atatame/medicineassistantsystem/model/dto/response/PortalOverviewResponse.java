package com.atatame.medicineassistantsystem.model.dto.response;

import com.atatame.medicineassistantsystem.model.entity.Project;
import com.atatame.medicineassistantsystem.model.entity.UserTask;
import lombok.Data;

import java.util.List;

@Data
public class PortalOverviewResponse {
    private List<UserTask> todayTasks;
    private List<Project> myProjects;
    private Long riskWarnings;
}

