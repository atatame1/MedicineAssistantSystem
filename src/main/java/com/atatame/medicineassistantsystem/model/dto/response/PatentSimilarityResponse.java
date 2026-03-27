package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PatentSimilarityResponse {
    private Long patentId;
    private String patentName;
    private Double score;
    private List<String> reasons;
    private String riskLevel;
}

