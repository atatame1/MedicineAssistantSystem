package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Integer priority;
    private Integer status;
    private Long projectId;
    private String projectName;
    private Long assigneeId;
    private LocalDateTime deadline;
    private String completionFeedback;
    private LocalDateTime createTime;
    private Boolean overdue;
}
