package com.atatame.medicineassistantsystem.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.atatame.medicineassistantsystem.utils.ChatMemoryUtil;

@Component
public class ReportGenerationAgent extends BasePromptAgent {
    public ReportGenerationAgent(ChatClient.Builder builder,
                                 @Value("classpath:prompts/report-generation.txt") Resource prompt,
                                 ChatMemoryUtil chatMemoryUtil) {
        super(builder, prompt, chatMemoryUtil);
    }

    @Override
    public AgentCode code() {
        return AgentCode.REPORT_GENERATION;
    }
}

