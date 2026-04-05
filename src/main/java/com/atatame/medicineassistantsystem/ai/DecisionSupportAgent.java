package com.atatame.medicineassistantsystem.ai;

import com.atatame.medicineassistantsystem.utils.ChatMemoryUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DecisionSupportAgent extends BasePromptAgent {
    public DecisionSupportAgent(ChatClient.Builder builder,
                                @Value("classpath:prompts/decision-support.txt") Resource prompt,
                                ChatMemoryUtil chatMemoryUtil) {
        super(builder, prompt, chatMemoryUtil);
    }

    @Override
    public AgentCode code() {
        return AgentCode.DECISION_SUPPORT;
    }
}
