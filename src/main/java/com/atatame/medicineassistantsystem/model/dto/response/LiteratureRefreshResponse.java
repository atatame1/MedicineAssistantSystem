package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LiteratureRefreshResponse {
    private String status;
    private String message;
    private String source;
    private LocalDateTime triggerTime;
}

