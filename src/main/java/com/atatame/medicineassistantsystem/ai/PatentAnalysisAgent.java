package com.atatame.medicineassistantsystem.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.atatame.medicineassistantsystem.utils.ChatMemoryUtil;

@Component
public class PatentAnalysisAgent extends BasePromptAgent {
    public PatentAnalysisAgent(ChatClient.Builder builder,
                               @Value("classpath:prompts/patent-analysis.txt") Resource prompt,
                               ChatMemoryUtil chatMemoryUtil) {
        super(builder, prompt, chatMemoryUtil);
    }

    @Override
    public AgentCode code() {
        return AgentCode.PATENT_ANALYSIS;
    }
}

