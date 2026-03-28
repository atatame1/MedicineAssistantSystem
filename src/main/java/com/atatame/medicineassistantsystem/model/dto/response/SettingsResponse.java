package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

@Data
public class SettingsResponse {
    private Long userId;
    private String preferences;
}
