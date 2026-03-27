package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegulationReminderResponse {
    private Long id;
    private String name;
    private String regulationNumber;
    private String followUpStatus;
    private LocalDateTime effectiveDate;
    private String importance;
    private String impactAssessment;
}

