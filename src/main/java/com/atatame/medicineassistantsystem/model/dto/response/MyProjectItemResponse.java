package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyProjectItemResponse {
    private Long id;
    private String projectName;
    private String herbName;
    private String formulaName;
    private String indication;
    private String phase;
    private Long projectorId;
    private String projectorName;
    private String currentUserRole;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime plannedEndTime;
    private LocalDateTime actualEndTime;
    private String description;
    private Double budget;
    private Integer priority;
    private String aiAssess;
    private String aiReport;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
