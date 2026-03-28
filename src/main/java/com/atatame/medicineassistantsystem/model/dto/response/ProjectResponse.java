package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private Long id;
    private String projectName;
    private String herbName;
    private String formulaName;
    private String indication;
    private String phase;
    private Integer status;
    private Integer priority;
    private LocalDateTime startTime;
    private LocalDateTime plannedEndTime;
    private String description;
    private Double budget;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

