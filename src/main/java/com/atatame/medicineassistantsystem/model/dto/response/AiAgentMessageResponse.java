package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiAgentMessageResponse {
    private String role;
    private String content;
    private LocalDateTime createTime;
}
