package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.model.dto.response.AiTaskResponse;
import reactor.core.publisher.Flux;

public interface IAiAgentService {
    AiTaskResponse run(AgentCode code, String input);
    Flux<String> stream(AgentCode code, String input, Long conversationId);
}

