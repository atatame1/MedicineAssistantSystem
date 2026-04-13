package com.atatame.medicineassistantsystem.model.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserTaskCreateRequest {
    private Long projectId;
    private Long userId;
    private String title;
    private String description;
    private Integer priority;
    private LocalDate deadline;
}

