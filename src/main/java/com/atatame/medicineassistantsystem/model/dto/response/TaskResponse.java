package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String taskType;
    private String priority;
    private String status;
    private Long projectId;
    private String projectName;
    private LocalDateTime deadline;
    private LocalDateTime createTime;
    private Boolean overdue;
    private List<String> tags;
}

