package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

@Data
public class UserStatisticsResponse {
    private Long totalTasks;
    private Long pendingTasks;
    private Long inProgressTasks;
    private Long completedTasks;
    private Long totalProjects;
    private Long activeProjects;
    private Long totalFavorites;
    private Long totalDocuments;
}

