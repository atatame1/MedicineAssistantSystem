package com.atatame.medicineassistantsystem.model.dto.request;

import lombok.Data;

@Data
public class ProjectEstablishmentDraftRequest {
    private String projectName;
    private String herbName;
    private String formulaName;
    private String indication;
    private String description;
    private Double budget;
    private String priority;
}
