package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

@Data
public class PatentRiskResponse {
    private Long patentId;
    private String patentName;
    private String riskLevel;
    private String riskDescription;
    private String suggestion;
}

