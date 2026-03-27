package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

@Data
public class LiteratureSummaryResponse {
    private Long id;
    private String title;
    private String researchType;
    private String methodology;
    private String mainFindings;
    private String conclusion;
    private String innovations;
    private String keywords;
}

