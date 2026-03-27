package com.atatame.medicineassistantsystem.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class MechanismInferenceAgent extends BasePromptAgent {
    public MechanismInferenceAgent(ChatClient.Builder builder,
                                   @Value("classpath:prompts/mechanism-inference.txt") Resource prompt) {
        super(builder, prompt);
    }

    @Override
    public AgentCode code() {
        return AgentCode.MECHANISM_INFERENCE;
    }
}

