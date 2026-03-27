package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.ai.AgentCode;
import com.atatame.medicineassistantsystem.model.dto.response.AiTaskResponse;

public interface IAiAgentService {
    AiTaskResponse run(AgentCode code, String input);
}

