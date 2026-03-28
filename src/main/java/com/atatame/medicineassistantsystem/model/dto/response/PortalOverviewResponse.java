package com.atatame.medicineassistantsystem.model.dto.response;

import com.atatame.medicineassistantsystem.model.entity.Project;
import lombok.Data;

import java.util.List;

@Data
public class PortalOverviewResponse {
    private List<TaskResponse> todayTasks;
    private List<Project> myProjects;
    private Long riskWarnings;
}

