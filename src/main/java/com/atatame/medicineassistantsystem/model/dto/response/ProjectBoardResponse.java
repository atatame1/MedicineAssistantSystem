package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

@Data
public class ProjectBoardResponse {
    private Long projectId;
    private String currentPhase;
    private Integer status;
    private Double progressPercent;
}
