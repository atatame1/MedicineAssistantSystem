package com.atatame.medicineassistantsystem.model.dto.response;

import com.atatame.medicineassistantsystem.model.entity.ProjectDecision;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionCompareResponse {
    private ProjectDecision first;
    private ProjectDecision second;
}
