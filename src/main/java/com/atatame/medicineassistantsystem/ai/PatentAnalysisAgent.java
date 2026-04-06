package com.atatame.medicineassistantsystem.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.atatame.medicineassistantsystem.utils.ChatMemoryUtil;

@Component
public class PatentAnalysisAgent extends BasePromptAgent {
    public PatentAnalysisAgent(ChatClient.Builder builder,
                               @Value("classpath:prompts/patent-analysis.txt") Resource prompt,
                               ChatMemoryUtil chatMemoryUtil,
                               ChatModel chatModel) {
        super(builder, prompt, chatMemoryUtil, chatModel);
    }

    @Override
    public AgentCode code() {
        return AgentCode.PATENT_ANALYSIS;
    }
}

