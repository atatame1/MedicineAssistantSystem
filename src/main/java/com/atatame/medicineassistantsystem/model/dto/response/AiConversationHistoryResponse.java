package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiConversationHistoryResponse {
    private String type;
    private Long conversationId;
    private String title;
    private String inputText;
    private String outputText;
    private LocalDateTime createTime;
}

