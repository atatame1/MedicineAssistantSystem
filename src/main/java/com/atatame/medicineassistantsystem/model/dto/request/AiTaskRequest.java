package com.atatame.medicineassistantsystem.model.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AiTaskRequest {
    private String input;
    private Long conversationId;
}

