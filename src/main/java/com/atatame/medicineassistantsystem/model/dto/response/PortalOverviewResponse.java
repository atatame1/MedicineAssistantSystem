package com.atatame.medicineassistantsystem.model.dto.response;

import com.atatame.medicineassistantsystem.model.entity.Project;
import lombok.Data;

import java.util.List;

@Data
public class PortalOverviewResponse {
    private List<TaskResponse> tasks;
    private List<Project> myProjects;
    private Long riskWarnings;
    private String summaryText;
}

